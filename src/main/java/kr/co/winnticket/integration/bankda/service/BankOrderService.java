package kr.co.winnticket.integration.bankda.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.bankda.dto.BankConfirmRequest;
import kr.co.winnticket.integration.bankda.dto.BankConfirmResponse;
import kr.co.winnticket.integration.bankda.dto.BankOrderDetailResponse;
import kr.co.winnticket.integration.bankda.dto.BankOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankOrderService {

    public BankOrderResponse getUnpaidOrders() {

        BankOrderResponse res = new BankOrderResponse();

        BankOrderResponse.Order order = new BankOrderResponse.Order();

        order.setOrder_id("20260209221233-01");
        order.setBuyer_name("이민경");
        order.setBilling_name("이민경");
        order.setBank_account_no("3333020387090");
        order.setBank_code_name("카카오뱅크");
        order.setOrder_price_amount(1900);

        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        order.setOrder_date(now);

        BankOrderResponse.Item item1 = new BankOrderResponse.Item();
        item1.setProduct_name("커피 200개");

        BankOrderResponse.Item item2 = new BankOrderResponse.Item();
        item2.setProduct_name("마스크 100개");

        order.setItems(List.of(item1, item2));

        res.setOrders(List.of(order));

        return res;
    }

    public BankOrderDetailResponse getOrderDetail(String orderId) {

        // 👉 여기서 DB 조회한다고 보면 됨.

        if (!orderId.equals("20260209224312-01")) {
            throw new RuntimeException("존재하지 않는 주문번호"); // → 415로 바꿔줄 예정
        }

        BankOrderDetailResponse res = new BankOrderDetailResponse();
        BankOrderDetailResponse.Order order = new BankOrderDetailResponse.Order();

        order.setOrder_id(orderId);
        order.setBuyer_name("홍길동");
        order.setBilling_name("홍길동");
        order.setBank_account_no("53000101123456");
        order.setBank_code_name("농협");
        order.setOrder_price_amount(19000);
        order.setOrder_date("2026-02-09 22:43:12");

        BankOrderDetailResponse.Item item1 = new BankOrderDetailResponse.Item();
        item1.setProduct_name("커피 200개");

        BankOrderDetailResponse.Item item2 = new BankOrderDetailResponse.Item();
        item2.setProduct_name("마스크 100개");

        order.setItems(List.of(item1, item2));
        res.setOrder(order);

        return res;
    }

    @Transactional
    public BankConfirmResponse confirmOrders(BankConfirmRequest req) {

        List<BankConfirmResponse.OrderResult> results = new ArrayList<>();

        for (BankConfirmRequest.ConfirmItem item : req.getRequests()) {

            String orderId = item.getOrder_id();

            // 여기서 DB 조회
            String status = findOrderStatus(orderId);

            if (status == null) {
                results.add(BankConfirmResponse.OrderResult.builder()
                        .order_id(orderId)
                        .description("요청된 주문번호가 없는 경우")
                        .build());

                continue;
            }

            if (!status.equals("WAIT_DEPOSIT")) {

                results.add(BankConfirmResponse.OrderResult.builder()
                        .order_id(orderId)
                        .description("요청된 주문번호가 입금대기 상태가 아닌 경우")
                        .build());

                continue;
            }

            // 상태 변경 (입금완료)
            updateOrderStatus(orderId, "PAID");

            results.add(BankConfirmResponse.OrderResult.builder()
                    .order_id(orderId)
                    .description("성공")
                    .build());
        }

        return BankConfirmResponse.builder()
                .return_code(200)
                .description("정상")
                .orders(results)
                .build();
    }

    private String findOrderStatus(String orderId) {

        // TODO DB 조회

        if (orderId.equals("20260209224312-01"))
            return "WAIT_DEPOSIT";

        if (orderId.equals("20260209224312-02"))
            return "PAID";

        return null;
    }

    private void updateOrderStatus(String orderId, String status) {

        // TODO DB update
    }
}
