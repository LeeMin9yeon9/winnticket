package kr.co.winnticket.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.cart.dto.mapperDto.OptionValueViewDto;
import kr.co.winnticket.cart.dto.mapperDto.ProductCartViewDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartAddReqDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartItemResDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartOptionResDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartResDto;
import kr.co.winnticket.cart.dto.sessionDto.CartItemSessionDto;
import kr.co.winnticket.cart.dto.sessionDto.CartOptionSessionDto;
import kr.co.winnticket.cart.mapper.ShopCartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopCartService {

    private final ShopCartMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CART_SESSION_KEY = "SHOP_CART";

    // ㅈㅏㅇㅂㅏㄱㅜㄴㅣ ㅇㅣㅁㅣㅈㅣ ㄱㅡㄹㅆㅣ ㅇㅗㅐㅇㅣㄹㅐ
    private List<String> parseImageUrls(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(
                    imageUrl,
                    new TypeReference<List<String>>() {}
            );
        } catch (Exception e) {
            // JSON 깨졌을 경우 방어
            return Collections.emptyList();
        }
    }

    // 장바구니 조회
    public List<CartItemSessionDto> getCart(HttpSession session) {
        List<CartItemSessionDto> cart =
                (List<CartItemSessionDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    // 장바구니 추가
    public void addCart(HttpSession session, ShopCartAddReqDto model) {
        List<CartItemSessionDto> cart = getCart(session);

        if (model.getOptions() == null || model.getOptions().isEmpty()) {
            throw new IllegalArgumentException("옵션이 없는 상품은 장바구니에 담을 수 없습니다.");
        }

        // 요청 옵션 → 세션 옵션으로 변환
        List<CartOptionSessionDto> sessionOptions =
                model.getOptions().stream()
                        .map(reqOpt -> {
                            CartOptionSessionDto opt = new CartOptionSessionDto();
                            opt.setOptionId(reqOpt.getOptionId());
                            opt.setOptionValueId(reqOpt.getOptionValueId());
                            return opt;
                        })
                        .toList();

        // 같은 상품 + 같은 옵션이면 수량 증가
        for (CartItemSessionDto c : cart) {
            if (c.getProductId().equals(model.getProductId())
                    && sameOptions(c.getOptions(), sessionOptions)) {

                c.setQuantity(c.getQuantity() + model.getQuantity());
                return;
            }
        }

        // 신규 장바구니 아이템 추가
        CartItemSessionDto item = new CartItemSessionDto();
        item.setId(UUID.randomUUID());
        item.setProductId(model.getProductId());
        item.setQuantity(model.getQuantity());
        item.setOptions(sessionOptions);

        cart.add(item);
    }
    private boolean sameOptions(
            List<CartOptionSessionDto> a,
            List<CartOptionSessionDto> b
    ) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        Set<UUID> aSet = a.stream()
                .map(CartOptionSessionDto::getOptionValueId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<UUID> bSet = b.stream()
                .map(CartOptionSessionDto::getOptionValueId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return aSet.equals(bSet);
    }



    // 장바구니 리스트
    public ShopCartResDto getCartView(HttpSession session) {

        List<CartItemSessionDto> cart = getCart(session);
        List<ShopCartItemResDto> items = new ArrayList<>();



        int orderAmount = 0; // 정가 총금액
        int discountAmount = 0;  // 할인금액 총 금액
        int finalAmount = 0;     // 결제 금액 합

        Iterator<CartItemSessionDto> iterator = cart.iterator();

        while (iterator.hasNext()) {
            CartItemSessionDto c = iterator.next();

            // 상품 조회
            ProductCartViewDto product = mapper.selectProduct(c.getProductId());
            System.out.println("PRODUCT = " + product);
            if (product == null) {
                iterator.remove();
                continue;
            }

            List<CartOptionSessionDto> sessionOptions =
                    (c.getOptions() == null) ? Collections.emptyList() : c.getOptions();

            // optionValueId 추출
            List<UUID> optionValueIds = sessionOptions.stream()
                    .map(CartOptionSessionDto::getOptionValueId)
                    .filter(Objects::nonNull)
                    .toList();

            System.out.println("SESSION OPTIONS = " + sessionOptions);

            System.out.println("OPTION VALUE IDS = " + optionValueIds);
            // 옵션 조회 (비어있으면 조회 안 함)
            List<OptionValueViewDto> options =
                    optionValueIds.isEmpty()
                            ? Collections.emptyList()
                            : Optional.ofNullable(mapper.selectOptionValues(optionValueIds))
                            .orElse(Collections.emptyList());

            System.out.println("OPTIONS FROM DB = " + options);


            int optionPrice = options.stream()
                    .mapToInt(OptionValueViewDto::getAdditionalPrice)
                    .sum();


            // 가격 계산
            int unitOriginPrice = product.getPrice() + optionPrice;          // 정가
            int unitFinalPrice  = product.getDiscountPrice() + optionPrice;  // 실제 결제 단가
            int unitDiscount    = unitOriginPrice - unitFinalPrice;          // 할인금액

            int quantity = c.getQuantity();

            int itemOrderPrice = unitOriginPrice * quantity;
            int itemDiscount   = unitDiscount * quantity;
            int itemFinalPrice = unitFinalPrice * quantity;

            orderAmount    += itemOrderPrice;
            discountAmount += itemDiscount;
            finalAmount    += itemFinalPrice;

            ShopCartItemResDto res = new ShopCartItemResDto();
            res.setId(c.getId());                       // 장바구니 ID
            res.setProductId(product.getId());          // 상품 ID
            res.setProductName(product.getName());      // 상품 이름
            List<String> images = parseImageUrls(product.getImageUrl());
            res.setImageUrl(images.isEmpty() ? null : images.get(0));     // 상품 이미지
            res.setQuantity(c.getQuantity());           // 상품 수량

            res.setUnitOriginPrice(unitOriginPrice);   // 정가
            res.setDiscountPrice(unitDiscount);        // 할인 금액
            res.setUnitFinalPrice(unitFinalPrice);     // 실제판매가
            res.setItemTotalPrice(itemFinalPrice);     // 최종 금액

            res.setOptions(
                    options.stream().map(o -> {
                        ShopCartOptionResDto model = new ShopCartOptionResDto();
                        model.setOptionId(o.getOptionId());
                        model.setOptionValueId(o.getOptionValueId());
                        model.setOptionName(o.getOptionName());
                        model.setOptionValue(o.getOptionValue());
                        return model;
                    }).toList()

            );
            items.add(res);
        }

        ShopCartResDto result = new ShopCartResDto();
        result.setItems(items);
        result.setOrderAmount(orderAmount);   // 정가총합
        result.setDiscountAmount(discountAmount);  // 할인총하
        result.setFinalAmount(finalAmount); // 결제 총합

        return result;

    }


    // 장바구니 수량 변경
    public void updateQuantity(HttpSession session , UUID id, int quantity){
        getCart(session).stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .ifPresent(i->i.setQuantity(quantity));
    }


    // 장바구니 삭제
    public void deleteItem(HttpSession session , UUID id){
        getCart(session).removeIf(i->i.getId().equals(id));
    }

    // 장바구니 수량 api용
    public int getCartCount(HttpSession session) {
        List<CartItemSessionDto> cart =
                (List<CartItemSessionDto>) session.getAttribute("SHOP_CART");

        if (cart == null) return 0;

        return cart.stream()
                .mapToInt(CartItemSessionDto::getQuantity)
                .sum();
    }
    // 주문 완료 시 장바구니 비우기
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }


}
