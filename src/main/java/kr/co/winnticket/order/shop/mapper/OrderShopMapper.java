package kr.co.winnticket.order.shop.mapper;

import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.shop.dto.OrderQrCouponGetResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface OrderShopMapper {
    // 주문정보조회
    OrderShopGetResDto selectOrderShop(UUID channelId, String orderNumber);

    // 주문상품조회
    List<OrderProductListGetResDto> selectOrderProductList(UUID id);

    // 주문테이블 insert
    Map<String, Object> insertOrder(
            @Param("channelId") UUID channelId,
            @Param("customerName") String customerName,
            @Param("customerPhone") String customerPhone,
            @Param("recipientName") String recipientName,
            @Param("recipientPhone") String recipientPhone,
            @Param("customerEmail") String customerEmail,
            @Param("companyName") String companyName,
            @Param("memo") String memo,
            @Param("totalPrice") int totalPrice,
            @Param("discountPrice") int discountPrice,
            @Param("paymentMethod") String paymentMethod,
            @Param("benepiaId") String benepiaId
    );

    // 주문별 상품별 insert
    UUID insertOrderItem(
            @Param("orderId") UUID orderId,
            @Param("productId") UUID productId,
            @Param("productName") String productName,
            @Param("quantity") int quantity,
            @Param("unitPrice") int unitPrice,
            @Param("totalPrice") int totalPrice,
            @Param("partnerId") UUID partnerId);

    // 주문 상품별 옵션 insert
    void insertOrderItemOption(
            @Param("orderItemId") UUID orderItemId,
            @Param("optionName") String optionName,
            @Param("optionValueName") String optionValueName,
            @Param("optionValueId") UUID optionValueId,
            @Param("additionalPrice") int additionalPrice
    );

    // 총 가격 update
    void updateOrderPrice(
            @Param("orderId") UUID orderId,
            @Param("finalPrice") int finalPrice,
            @Param("pointAmount") int pointAmount
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


    // 티켓주문 정보 저장
    void insertOrderTicket(
            @Param("orderItemId") UUID orderItemId,
            @Param("ticketNumber") String ticketNumber
    );

    // KCP 포인트 즉시 승인 처리
    int updatePointPaymentApproved(
            @Param("orderNumber") String orderNumber,
            @Param("pgTid") String pgTid,
            @Param("approvalNo") String approvalNo
    );

    // KCP 포인트 결제 실패
    int updatePaymentFailed(
            @Param("orderNumber") String orderNumber
    );

    // 문자 쿠폰 번호 조회
    OrderQrCouponGetResDto selectOrderQrInfo(@Param("orderNumber") String orderNumber);

    // 문자 QR
    List<OrderQrCouponGetResDto.Ticket> selectTicketsByOrderNumber(@Param("orderNumber") String orderNumber);

    // 입금기한 설정
    void updateDepositDeadline(
            @Param("orderId") UUID orderId,
            @Param("deadline") LocalDateTime deadline
    );

    // 입금기한 초과 주문
    List<String> findExpiredOrderNumbers();

    // 상태 선점 (중복 방지)
    int updateToCanceling(@Param("orderNumber") String orderNumber);

    // 최종 취소 완료
    int updateExpireCompleted(@Param("orderNumber") String orderNumber);

    // 실패 시 롤백
    int rollbackCanceling(@Param("orderNumber") String orderNumber);

    // 주문 후 재고 차감
    int updateOptionValueStock(@Param("optionValueId") UUID optionValueId, @Param("quantity") int quantity);


}
