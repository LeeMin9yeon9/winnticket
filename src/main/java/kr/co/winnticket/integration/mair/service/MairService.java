package kr.co.winnticket.integration.mair.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.mair.client.MairCouponClient;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.mapper.MairResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MairService {

    private final MairOrderMapper mapper;
    private final MairCouponClient client;
    private final MairResponseMapper responseMapper;

    // 쿠폰 발송 요청
    public List<MairCouponResDto> issueTickets(String orderNumber) {

        // 주문 기본정보 조회
        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if (order == null) {
            log.info("[MAIR] 주문이 존재하지 않습니다.");
            throw new RuntimeException("주문 없음 orderNumber=" + orderNumber);
        }

        // 결제 완료 주문만 발송
        if (!"PAID".equals(order.getPaymentStatus())) {
            log.info("[MAIR] 결제 완료 주문만 쿠폰 발송 가능합니다.");
            throw new IllegalStateException("결제 완료 주문만 쿠폰 발송 가능합니다.");
        }

        // 주문상품 조회
        List<MairOrderItemInfoDto> items = mapper.selectOrderItemInfos(order.getOrderId());

        if (items == null || items.isEmpty()) {
            log.info("엠에어 상품 없음 orderNumber={}", orderNumber);
            return List.of();
        }

        List<MairCouponResDto> results = new ArrayList<>();

        for (MairOrderItemInfoDto item : items) {

            int remain = mapper.countRemainTickets(item.getOrderItemId());

            log.info("[MAIR] 발권 대상 orderItemId={}, productCode={}, remain={}",
                    item.getOrderItemId(),
                    item.getProductCode(),
                    remain);

            if (remain == 0) {
                log.info("[MAIR] 이미 발권 완료 orderItemId={}", item.getOrderItemId());
                continue;
            }

            for (int i = 0; i < remain; i++) {

                // 엠에어 발권
                MairCouponResDto res = client.issue(
                        item.getProductCode(),
                        orderNumber,
                        order.getCustomerName(),
                        order.getCustomerPhone()
                );
                log.info("[MAIR] 응답 result={}, TNO={}", res.getResult(), res.getTno());

                if (!"OK".equals(res.getResult())) {
                    throw new RuntimeException("엠에어 발권 실패 result=" + res.getResult());
                }

                mapper.updatePartnerOrderCode(
                        item.getOrderItemId(),
                        res.getTno()
                );


                log.info("[MAIR] 쿠폰 저장 완료 TNO={}", res.getTno());
                results.add(res);
            }
        }
        return results;
    }


    // 취소
    public List<IntegrationResult> cancelByOrder(String orderNumber) {

        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if (order == null) {
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        List<MairOrderItemInfoDto> items = mapper.selectOrderItemInfos(order.getOrderId());

        List<IntegrationResult> results = new ArrayList<>();

        for (MairOrderItemInfoDto item : items) {

            if (item.getProductCode() == null) {
                continue;
            }

            String trno = orderNumber;

            log.info("[MAIR][CANCEL] REQUEST ITCD={}, TRNO={}",
                    item.getProductCode(),
                    trno);

            MairCouponResDto res = client.cancel(item.getProductCode(), trno);

            log.info("[MAIR][CANCEL] RESPONSE={}", res);

            IntegrationResult result = responseMapper.mapCancel(res);

            log.info("[MAIR][CANCEL] RESULT code={}, message={}",
                    result.getCode(),
                    result.getMessage());

            results.add(result);

            if(!result.isSuccess()){
                throw new RuntimeException(
                        "엠에어 취소 실패 code="
                                + result.getCode()
                                + " message="
                                + result.getMessage());
            }
        }
        return results;
    }



    // 사용여부 확인
    public IntegrationResult useCheckByOrderNumber(String orderNumber) {

        log.info("[MAIR][USE-CHECK] START orderNumber={}", orderNumber);

        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if (order == null) {
            log.error("[MAIR][USE-CHECK] 주문 없음 orderNumber={}", orderNumber);
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        List<MairOrderItemInfoDto> items = mapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {

            if (item.getProductCode() == null || item.getProductCode().isBlank()) {
                continue;
            }

            log.info("[MAIR][USE-CHECK] REQUEST ITCD={}, TRNO={}", item.getProductCode(), orderNumber);

            MairCouponResDto res = client.useCheck(item.getProductCode(), orderNumber);

            log.info("[MAIR][USE-CHECK] RESPONSE={}", res);

            IntegrationResult result = responseMapper.mapUseCheck(res);

            log.info("[MAIR][USE-CHECK] RESULT code={}, message={}",
                    result.getCode(),
                    result.getMessage());

            return result;   // ⭐ 반드시 반환
        }

        throw new RuntimeException("조회 가능한 상품이 없습니다.");
    }

    // 휴대폰 번호 정규화
    private String normalizeHp(String hp) {
        return hp == null ? null : hp.replaceAll("[^0-9]", "");
    }
}
