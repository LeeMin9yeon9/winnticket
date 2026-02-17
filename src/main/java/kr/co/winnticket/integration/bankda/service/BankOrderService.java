package kr.co.winnticket.integration.bankda.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.integration.bankda.dto.*;
import kr.co.winnticket.integration.bankda.exception.BankdaException;
import kr.co.winnticket.integration.bankda.mapper.BankOrderMapper;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankOrderService {

    private final BankOrderMapper bankOrderMapper;
    private final OrderService orderService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 미입금 주문 조회
     */
    public BankOrderResponse selectBankdaOrders() {
        List<BankOrderResponse.Order> orders =
                bankOrderMapper.selectBankdaOrders();

        BankOrderResponse res = new BankOrderResponse();
        res.setOrders(orders);

        return res;
    }

    /**
     * 주문 상세 조회
     */
    public BankOrderDetailResponse getOrderDetail(String orderId) {
        // 요청 형식 오류
        try {
            System.out.println("🔥 Service start");
        if (orderId == null || orderId.isBlank()) {
            throw new BankdaException(400, "요청 format 오류");
        }

        BankOrderDetailResponse.Order order =
                bankOrderMapper.selectBankOrderDetail(orderId);

        // 존재하지 않는 주문번호
        if (order == null) {
            throw new BankdaException(415, "존재하지 않는 주문번호");
        }

        BankOrderDetailResponse response = new BankOrderDetailResponse();
        response.setOrder(List.of(order));

        return response;
        } catch (Exception e) {
            System.out.println("🚨 예외 발생!");
            e.printStackTrace();   // 🔥 이거 반드시
            throw e;
        }
    }

    public ResponseEntity<BankConfirmResponse> confirm(
            BankConfirmRequest request
    ) {
        // 400 format 오류
        if (request == null || request.getRequests() == null || request.getRequests().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BankConfirmResponse(
                            400,
                            "요청 format 오류",
                            null
                    ));
        }

        List<BankConfirmResponse.OrderResult> results = new ArrayList<>();
        boolean hasInvalidOrderId = false;

        for (BankConfirmRequest.Request req : request.getRequests()) {

            String orderNumber = req.getOrderId();

            if (orderNumber == null) {
                results.add(new BankConfirmResponse.OrderResult(
                        null,
                        "요청 format 오류"
                ));
                hasInvalidOrderId = true;
                continue;
            }

            UUID orderId = bankOrderMapper.findOrderIdByOrderNumber(orderNumber);

            // 존재하지 않는 주문
            if (orderId == null) {
                results.add(new BankConfirmResponse.OrderResult(
                        orderNumber,
                        "존재하지 않는 주문"
                ));
                hasInvalidOrderId = true;
                continue;
            }

            OrderAdminDetailGetResDto order = orderService.selectOrderAdminDetail(orderId);

            // 입금대기 상태가 아닌 경우
            if (order.getPaymentStatus() != PaymentStatus.READY
                    || order.getStatus() != OrderStatus.PENDING_PAYMENT) {

                results.add(new BankConfirmResponse.OrderResult(
                        orderNumber,
                        "요청된 주문번호가 입금대기 상태가 아님"
                ));
                continue;
            }

            // 정상 처리 (관리자 로직 재사용)
            orderService.completePayment(orderId);

            results.add(new BankConfirmResponse.OrderResult(
                    orderNumber,
                    "성공"
            ));
        }

        int returnCode = hasInvalidOrderId ? 415 : 200;
        String desc = hasInvalidOrderId ? "order_id 오류" : "정상";

        return ResponseEntity.ok(
                new BankConfirmResponse(
                        returnCode,
                        desc,
                        results
                )
        );
    }

}
