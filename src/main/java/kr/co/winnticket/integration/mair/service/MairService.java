package kr.co.winnticket.integration.mair.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.mair.client.MairCouponClient;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.props.MairProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class MairService {

    private final MairOrderMapper mairOrderMapper;
    private final MairCouponClient mairCouponClient;
    private final MairProperties mairProperties;

    // 결제 완료 티켓 발송
    @Transactional
    public List<MairCouponResDto> issueTickets(String orderNumber) {

        log.info("[MAIR] issueTickets start orderId={}", orderNumber);

        // Swagger 응답 리스트
        List<MairCouponResDto> resultList = new ArrayList<>();


        // 주문 조회
        MairOrderInfoDto order =
                mairOrderMapper.selectOrderInfo(orderNumber);

        if (order == null) {
            log.error("[MAIR] 주문 없음 orderNumber={}", orderNumber);
            throw new IllegalArgumentException("주문이 없습니다.");
        }


        // 결제 완료 상태만 발송
        if (!"PAID".equals(order.getPaymentStatus())) {
            log.warn("[MAIR] 발송 스킵 paymentStatus={}", order.getPaymentStatus());
            return resultList;
        }

        log.info("[MAIR] 주문 확인 orderId={}, customer={}, phone={}", order.getOrderId(), order.getCustomerName(), order.getCustomerPhone());


        // 주문 아이템 조회
        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {

            String itcd = item.getProductCode();

            // 상품코드 없으면 스킵
            if (itcd == null || itcd.isBlank()) {

                log.warn("[MAIR] 상품코드 없음 orderItemId={}", item.getOrderItemId());
                continue;
            }


            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();

            if (quantity <= 0) continue;


            log.info("[MAIR] 발송 대상 orderItemId={}, itcd={}, qty={}", item.getOrderItemId(), itcd, quantity);



            // 이미 발송된 수량 조회
            int issuedCount = mairOrderMapper.countOrderTickets(item.getOrderItemId());

            if (issuedCount >= quantity) {

                log.info("[MAIR] 이미 발송 완료 orderItemId={}", item.getOrderItemId());

                MairCouponResDto res = new MairCouponResDto();
                res.setResult("ALREADY_ISSUED");
                res.setTno(null);

                resultList.add(res);
                continue;
            }

            int need = quantity - issuedCount;

            // 부족한 수량만큼 발송
            for (int i = 0; i < need; i++) {

                log.info("[MAIR] 쿠폰 발송 요청 itcd={}, orderNumber={}", itcd, orderNumber);


                MairCouponResDto res = mairCouponClient.issue(
                        itcd,
                        orderNumber,
                        order.getCustomerName(),
                        normalizeHp(
                                order.getCustomerPhone()
                        )
                );

                // 응답 실패
                if (res == null || !res.isOk()) {

                    log.error(
                            "[MAIR] 발송 실패 orderNumber={}, itcd={}, result={}",
                            orderNumber,
                            itcd,
                            res == null ? "null" : res.getResult()
                    );

                    throw new IllegalStateException(
                            "엠에어 발송 실패"
                    );
                }

                String ticketNo = res.getTno();

                if ("dev".equalsIgnoreCase(mairProperties.getMode())) {
                    log.info("[MAIR] DEV mode 응답 result={}, tno={}", res.getResult(), ticketNo);

                    // dev는 TNO 없으면 임시 생성
                    if (ticketNo == null || ticketNo.isBlank()) {

                        ticketNo = generateTempTicketNo(
                                        orderNumber,
                                        item.getOrderItemId()
                                );

                        res.setTno(ticketNo);

                        log.info("[MAIR] DEV 임시 쿠폰 생성 ticketNo={}", ticketNo);}
                }

                else {
                    log.info("[MAIR] PROD mode 응답 result={}, tno={}",
                            res.getResult(),
                            ticketNo
                    );

                    if (ticketNo == null || ticketNo.isBlank()) {
                        log.error("[MAIR] PROD 쿠폰번호 없음 orderNumber={}", orderNumber);

                        throw new IllegalStateException(
                                "쿠폰번호 없음"
                        );
                    }
                }

                // DB 저장
                mairOrderMapper.insertOrderTicket(String.valueOf(item.getOrderItemId()),
                        ticketNo
                );


                log.info("[MAIR] 쿠폰 저장 완료 orderItemId={}, ticketNo={}", item.getOrderItemId(), ticketNo);

                // Swagger 응답 추가
                resultList.add(res);
            }

        }

        log.info("[MAIR] 발송 완료 orderNumber={}, count={}", orderNumber, resultList.size());
        return resultList;
    }



    // 쿠폰 취소
    @Transactional
    public void cancelByOrder(String orderNumber , UUID orderItemId){

        int cancelableCount = mairOrderMapper.countCancelableTickets(orderItemId);
        if (cancelableCount <= 0) {
            throw new IllegalStateException("취소 가능한 티켓이 없습니다. (이미 사용/취소됨) orderItemId=" + orderItemId);
        }
        // 주문 조화
        MairOrderInfoDto dto = mairOrderMapper.selectOrderInfo(orderNumber);
        if(dto == null){
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        // 2) ITCD 찾기
        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(dto.getOrderId());
        MairOrderItemInfoDto target = items.stream()
                .filter(i -> orderItemId.equals(i.getOrderItemId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[MAIR]주문아이템 없음 orderItemId=" + orderItemId));

        String itcd = target.getProductCode();
        if (itcd == null || itcd.isBlank()) {
            throw new IllegalStateException("[MAIR]ITCD 없음(orderItemId=" + orderItemId + ")");
        }

        // 3) 엠에어 취소 호출
        MairCouponResDto res = mairCouponClient.cancel(itcd, orderNumber);

        if (res == null || !"OK".equalsIgnoreCase(res.getResult())) {
            throw new IllegalStateException("[MAIR] 취소 실패 orderNumber=" + orderNumber
                    + ", itcd=" + itcd
                    + ", result=" + (res == null ? "null" : res.getResult()));
        }

        // 4)미사용+미취소만 취소처리
        int updated = mairOrderMapper.updateTicketsCanceled(orderItemId);

        if (updated <= 0) {
            throw new IllegalStateException("[MAIR]취소 처리할 티켓이 없습니다. (이미 사용/취소됨) orderItemId=" + orderItemId);
        }
    }

    // 사용 여부 확인
    public void useCheckByOrderNumber(String orderNumber) {

        MairOrderInfoDto order = mairOrderMapper.selectOrderInfo(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("[MAIR]주문이 없습니다. orderNumber=" + orderNumber);
        }

        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {
            String itcd = item.getProductCode();
            if (itcd == null || itcd.isBlank()) continue;

            MairCouponResDto res = mairCouponClient.useCheck(itcd, orderNumber);

            if (res == null) {
                throw new IllegalStateException("[MAIR] 사용여부 조회 응답 null orderNumber=" + orderNumber + ", itcd=" + itcd);
            }

            System.out.println("[MAIR] useCheck orderNumber=" + orderNumber
                    + ", itcd=" + itcd
                    + ", result=" + res.getResult()
                    + ", tno=" + res.getTno());
        }
    }

    private String normalizeHp(String hp) {
        if (hp == null) return null;
        return hp.replaceAll("[^0-9]", "");
    }

    // 쿠폰 테스트용
    private String generateTempTicketNo(String orderNumber, UUID orderItemId) {
        return "DEV-MAIR-"
                + orderNumber
                + "-"
                + orderItemId.toString().substring(0, 8)
                + "-"
                + System.currentTimeMillis();
    }
}
