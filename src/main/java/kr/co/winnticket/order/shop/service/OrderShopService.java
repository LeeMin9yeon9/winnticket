package kr.co.winnticket.order.shop.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.cart.service.ShopCartService;
import kr.co.winnticket.channels.channel.mapper.ChannelMapper;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShopService {
    private final ProductMapper productMapper;
    private final OrderShopMapper mapper;
    private final ShopCartService shopCartService;
    private final ChannelMapper channelMapper;
    private final PayletterService paymentService;


    public OrderShopGetResDto selectOrderShop(String orderNumber) {
        OrderShopGetResDto model = mapper.selectOrderShop(orderNumber);

        if (model == null) {
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        model.setProducts(mapper.selectOrderProductList(model.getId()));
        return model;
    }

    @Transactional
    public OrderCreateResDto createOrder(OrderCreateReqDto reqDto, HttpSession session) {
        try {
        log.info("createOrder start, channelId={}", reqDto.getChannelId());

        Boolean useCard = channelMapper.selectUseCardById(reqDto.getChannelId());
        Boolean cardAllowed = (useCard != null && useCard);

            // 결제수단 결정 (카드 미허용 채널이면 무조건 무통장으로 보정)
            PaymentMethod paymentMethod = reqDto.getPaymentMethod();
            if (!cardAllowed) {
                paymentMethod = PaymentMethod.VIRTUAL_ACCOUNT;
            }


        // 주문 테이블 생성(입력한 정보들로)
        Map<String, Object> result = mapper.insertOrder(
            reqDto.getChannelId(),
            reqDto.getCustomerName(),
            reqDto.getCustomerPhone(),
            reqDto.getCustomerEmail(),
            reqDto.getTotalPrice(),
            reqDto.getDiscountPrice()
        );

        UUID orderId = (UUID) result.get("id");
        String orderNumber = (String) result.get("order_number");

        // 고른 각 상품들의 정보를 추출해서 각 상품의 정보를 추출
        for (OrderCreateReqDto.OrderItemReqDto item : reqDto.getItems()) {
            ProductDetailGetResDto product = productMapper.selectProductDetail(item.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("상품이 존재하지 않습니다.");
            }

            // 주문한 상품 정보 insert
            UUID orderItemId = mapper.insertOrderItem(
                    orderId,
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
            );

            // 각 옵션 가격 추출해서 가격을 구한 후 검증을 추가해야함(api 구현하면서 추가할것)
            // 각 상품별 옵션 정보 insert
            if (item.getOptions() != null) {
                for (OrderCreateReqDto.OrderItemOptionReqDto opt : item.getOptions()) {
                    ProductOptionGetResDto option = productMapper.selectProductOptionDetail(opt.getOptionId());
                    ProductOptionValueGetResDto optionValue = productMapper.selectOptionValueDetail(opt.getOptionValueId());

                    if (option == null || optionValue == null) {
                        throw new IllegalArgumentException("유효하지 않은 옵션입니다.");
                    }

                    mapper.insertOrderItemOption (
                        orderItemId,
                        option.getName(),
                        optionValue.getValue(),
                        optionValue.getAdditionalPrice()
                    );
                }
            }
        }

        // 옵션값에 대한 계산 다시 실행 후 테이블에 넣어야함(api 연동할때) 최종결제금액 업데이트 지금은 프론트에서 주는대로 총 금액 - 할인금액
        int finalPrice = reqDto.getTotalPrice() - reqDto.getDiscountPrice();

        mapper.updateOrderPrice(orderId, finalPrice);

        // 주문성공 시 장바구니 비우기
        shopCartService.clearCart(session);

        OrderCreateResDto resDto = new OrderCreateResDto();
        resDto.setOrderId(orderId);
        // resDto.setPaymentStatus("READY");
        resDto.setOrderNumber(orderNumber);
        resDto.setFinalPrice(finalPrice);

        // 무통장(일반/베네피아 가능)
        if(paymentMethod == PaymentMethod.VIRTUAL_ACCOUNT){
            resDto.setPaymentStatus("READY");
            return resDto;
        }
            if (paymentMethod == PaymentMethod.CARD) {
                PayletterPaymentResDto payRes = paymentService.paymentRequest(
                        orderId,
                        orderNumber,
                        finalPrice,
                        reqDto.getCustomerName(),
                        reqDto.getCustomerEmail(),
                        reqDto.getCustomerPhone()
                );
                resDto.setOrderId(orderId);
            resDto.setPaymentStatus("REQUESTED");
            resDto.setPgProvider("PAYLETTER");
            resDto.setPgTid(String.valueOf(payRes.getToken()));
            resDto.setPgOnlineUrl(payRes.getOnlineUrl());
            resDto.setPgMobileUrl(payRes.getMobileUrl());
            return resDto;
        }
        resDto.setPaymentStatus("READY");
        return resDto;


        } catch (Exception e) {
            log.error("결재완료 중 오류 발생", e);
            throw e; // 다시 던짐 (중요)
        }
    }
}
