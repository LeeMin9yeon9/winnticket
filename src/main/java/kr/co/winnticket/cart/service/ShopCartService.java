package kr.co.winnticket.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.cart.dto.mapperDto.OptionValueViewDto;
import kr.co.winnticket.cart.dto.mapperDto.ProductCartViewDto;
import kr.co.winnticket.cart.dto.mapperDto.StayDatePriceDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartAddReqDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartItemResDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartOptionResDto;
import kr.co.winnticket.cart.dto.responseDto.ShopCartResDto;
import kr.co.winnticket.cart.dto.sessionDto.CartItemSessionDto;
import kr.co.winnticket.cart.dto.sessionDto.CartOptionSessionDto;
import kr.co.winnticket.cart.mapper.ShopCartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // 신규 장바구니 아이템 추가
        CartItemSessionDto item = new CartItemSessionDto();
        item.setId(UUID.randomUUID());
        item.setProductId(model.getProductId());
        item.setQuantity(model.getQuantity());

        // 숙박상품
        if (model.getStartDate() != null && model.getEndDate() != null) {

            if (model.getStartDate() == null || model.getEndDate() == null) {
                throw new IllegalArgumentException("체크인/체크아웃 날짜를 모두 입력해야 합니다.");
            }

            if (!model.getEndDate().isAfter(model.getStartDate())) {
                throw new IllegalArgumentException("체크아웃 날짜는 체크인 이후여야 합니다.");
            }

            if (model.getStayOptionValueId() == null) {
                throw new IllegalArgumentException("숙박 옵션값이 없습니다.");
            }

            for(CartItemSessionDto c : cart){
                if(
                        c.getProductId().equals(model.getProductId()) &&
                                Objects.equals(c.getStayOptionValueId(),model.getStayOptionValueId()) &&
                                Objects.equals(c.getStartDate(), model.getStartDate()) &&
                                Objects.equals(c.getEndDate(), model.getEndDate())
                ) {
                    c.setQuantity(c.getQuantity() + model.getQuantity());
                    return;
                }
            }

            item.setStayOptionValueId(model.getStayOptionValueId());
            item.setStartDate(model.getStartDate());
            item.setEndDate(model.getEndDate());

            cart.add(item);
            return;

        } else {

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

            item.setOptions(sessionOptions);

            cart.add(item);
        }
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

            List<UUID> stayPeriodIds = null;
            Integer groupNo = null;

            // 가격 계산

            int unitOriginPrice;
            int unitFinalPrice;
            int unitDiscount;


            if (c.getStartDate() != null && c.getEndDate() != null) {
                // 숙박 상품
                List<LocalDate> dates =
                        c.getStartDate()
                                .datesUntil(c.getEndDate()) // 체크아웃 미포함
                                .toList();

                List<StayDatePriceDto> prices =
                        mapper.selectStayDatePrices(
                                c.getStayOptionValueId(),
                                dates
                        );

                if (prices.size() != dates.size()) {
                    throw new IllegalStateException("가격이 설정되지 않은 날짜가 있습니다.");
                }


                // 숙박 정가 합계
                int originStayPrice = prices.stream()
                        .mapToInt(StayDatePriceDto::getPrice)
                        .sum();

                // 숙박 결제가 합계
                int finalStayPrice = prices.stream()
                        .mapToInt(p ->
                                p.getDiscountPrice() != null ? p.getDiscountPrice() : p.getPrice()
                                ).sum();

                // 장바구니 단가 계산
                unitOriginPrice = originStayPrice;
                unitFinalPrice = finalStayPrice;
                unitDiscount = originStayPrice - finalStayPrice;

                stayPeriodIds = prices.stream()
                        .map(StayDatePriceDto::getId)
                        .toList();

                groupNo = prices.get(0).getGroupNo();

            } else {
                // 일반 상품
                int optionPrice = options.stream()
                        .mapToInt(OptionValueViewDto::getAdditionalPrice)
                        .sum();

                unitOriginPrice = product.getPrice() + optionPrice;
                unitFinalPrice = product.getDiscountPrice() + optionPrice;
                unitDiscount = unitOriginPrice - unitFinalPrice;
            }

            int quantity = c.getQuantity();

            int itemOrderPrice = unitOriginPrice * quantity;
            int itemDiscount = unitDiscount * quantity;
            int itemFinalPrice = unitFinalPrice * quantity;

            orderAmount += itemOrderPrice;
            discountAmount += itemDiscount;
            finalAmount += itemFinalPrice;

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

            if (c.getStartDate() != null && c.getEndDate() != null) {
                // 숙박 상품 옵션
                ShopCartOptionResDto opt = new ShopCartOptionResDto();
                opt.setStayOptionValueId(c.getStayOptionValueId());
                opt.setStartDate(c.getStartDate());
                opt.setEndDate(c.getEndDate());
                opt.setStayPeriodIds(stayPeriodIds);
                opt.setGroupNo(groupNo);

                res.setOptions(List.of(opt));
            } else {
                // 일반 상품 옵션
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
            }
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
