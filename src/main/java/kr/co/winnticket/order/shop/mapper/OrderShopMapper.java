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

    // Toss 결제 초기화 (주문 생성 시 pg_provider = TOSSPAYMENTS 설정)
    int updateTossPaymentInit(@Param("orderId") UUID orderId);

    // Toss 결제 승인 성공 처리 (paymentKey 저장, 중복 처리 방지)
    int updateTossConfirmSuccessIfNotPaid(
            @Param("orderId") UUID orderId,
            @Param("paymentKey") String paymentKey,
            @Param("payloadJson") String payloadJson
    );

    // 상품명 + 개수 조회 (주문명 생성용)
    Map<String, Object> selectProductSummary(@Param("orderId") UUID orderId);


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

    // 만료 주문 조회(스케줄러용)
    List<String> findExpiredOrderNumbers();

    // 주문 후 재고 차감
    int updateOptionValueStock(@Param("optionValueId") UUID optionValueId, @Param("quantity") int quantity);

    // 토스 결제 승인 후 실제 결제수단 업데이트 (카드/가상계좌/계좌이체 등)
    int updatePaymentMethod(@Param("orderId") UUID orderId, @Param("paymentMethod") String paymentMethod);
}
