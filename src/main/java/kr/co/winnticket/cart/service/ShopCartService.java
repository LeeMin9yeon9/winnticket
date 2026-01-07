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

@Service
@RequiredArgsConstructor
public class ShopCartService {

    private final ShopCartMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CART_SESSION_KEY = "SHOP_CART";

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

        // 장바구니에 같은 상품, 옵션 있으면 수량 증가
        for(CartItemSessionDto c : cart){
            if(c.getProductId().equals(model.getProductId()) && Objects.equals(c.getOptions(),model.getOptions())){
                c.setQuantity(c.getQuantity()+model.getQuantity());
                return;
            }
        }
            CartItemSessionDto item = new CartItemSessionDto();
            item.setId(UUID.randomUUID());
            item.setProductId(model.getProductId());
            item.setOptions(model.getOptions());
            item.setQuantity(model.getQuantity());
            cart.add(item);

            // 주문 콘솔 디버깅용ㅇㄴ머이ㅓㅁㄴㅇ
        for (CartOptionSessionDto o : model.getOptions()) {
            System.out.println("optionId = " + o.getOptionId());
            System.out.println("optionValueId = " + o.getOptionValueId());
        }

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
            List<UUID> optionValueIds = new ArrayList<>();
            for (CartOptionSessionDto o : sessionOptions) {
                if (o != null && o.getOptionValueId() != null) {
                    optionValueIds.add(o.getOptionValueId());
                }
            }

            // 옵션 조회 (비어있으면 조회 안 함)
            List<OptionValueViewDto> options =
                    optionValueIds.isEmpty()
                            ? Collections.emptyList()
                            : mapper.selectOptionValues(optionValueIds);

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
            res.setImageUrl(parseImageUrls(product.getImageUrl()));     // 상품 이미지
            res.setQuantity(c.getQuantity());           // 상품 수량

            res.setUnitOriginPrice(unitOriginPrice);   // 정가
            res.setDiscountPrice(unitDiscount);        // 할인 금액
            res.setUnitFinalPrice(unitFinalPrice);     // 실제판매가
            res.setItemTotalPrice(itemFinalPrice);     // 최종 금액

            res.setOptions(
                    options.stream().map(o -> {
                        ShopCartOptionResDto model = new ShopCartOptionResDto();
                        model.setOptionName(o.getOptionName());
                        model.setOptionValue(o.getValue());
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


}
