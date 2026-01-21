package kr.co.winnticket.order.shop.mapper;

import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface OrderShopMapper {
    // 주문정보조회
    OrderShopGetResDto selectOrderShop(String orderNumber);

    // 주문상품조회
    List<OrderProductListGetResDto> selectOrderProductList(UUID id);

    // 주문테이블 insert
    Map<String, Object> insertOrder(
            @Param("channelId") UUID channelId,
            @Param("customerName") String customerName,
            @Param("customerPhone") String customerPhone,
            @Param("customerEmail") String customerEmail,
            @Param("totalPrice") int totalPrice,
            @Param("discountPrice") int discountPrice,
            @Param("paymentMethod") String paymentMethod
    );

    // 주문별 상품별 insert
    UUID insertOrderItem(
            @Param("orderId") UUID orderId,
            @Param("productId") UUID productId,
            @Param("productName") String productName,
            @Param("quantity") int quantity,
            @Param("unitPrice") int unitPrice,
            @Param("totalPrice") int totalPrice
    );

    // 주문 상품별 옵션 insert
    void insertOrderItemOption(
            @Param("orderItemId") UUID orderItemId,
            @Param("optionName") String name,
            @Param("optionValueName") String value,
            @Param("additionalPrice") int additionalPrice
    );

    // 총 가격 update
    void updateOrderPrice(
            @Param("orderId") UUID orderId,
            @Param("finalPrice") int finalPrice
    );

    // payletter 결제요청 결과 저장
    int updatePayletterRequest(
            @Param("orderId") UUID orderId,
            @Param("pgProvider") String pgProvider,
            @Param("pgTid") String pgTid,
            @Param("pgOnlineUrl") String pgOnlineUrl,
            @Param("pgMobileUrl") String pgMobileUrl,
            @Param("pgCode") String pgCode
    );

    // payletter 상품명 + 개수
    Map<String, Object> selectPayletterProductSummary(@Param("orderId") UUID orderId);


    // payletter 주문 결제정보 조회(금액검증.취소)
    Map<String, Object> selectOrderPaymentInfo(@Param("orderId") UUID orderId);

    // 결제 성공 콜백 처리 중복 방지
    int updatePayletterCallbackSuccessIfNotPaid(
            @Param("orderId") UUID orderId,
            @Param("payloadJson") String payloadJson,
            @Param("tid") String tid,
            @Param("cid") String cid
    );

    //payletter 콜백 실패
    int updatePayletterCallbackFailed(
            @Param("orderId") UUID orderId,
            @Param("payloadJson") String payloadJson,
            @Param("failReason") String failReason
    );

    // payletter 취소 성공 처리
    int updatePayletterCancelSuccess(
            @Param("orderId") UUID orderId,
            @Param("payloadJson") String payloadJson
    );


}
