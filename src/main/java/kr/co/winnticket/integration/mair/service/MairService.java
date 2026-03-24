package kr.co.winnticket.integration.mair.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.mair.client.MairCouponClient;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.mapper.MairResponseMapper;
import kr.co.winnticket.integration.mair.props.MairResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
                String trno = orderNumber + "-" + (i + 1);

                // 엠에어 발권
                MairCouponResDto res = client.issue(
                        item.getProductCode(),
                        trno,
                        order.getCustomerName(),
                        order.getCustomerPhone()
                );
                log.info("[MAIR] 응답 TRNO={},result={}, TNO={}",trno, res.getResult(), res.getTno());

                if (!"OK".equals(res.getResult())) {
                    throw new RuntimeException("엠에어 발권 실패 result=" + res.getResult());
                }
                mapper.updatePartnerOrderNumber(item.getOrderItemId(), trno);

                // 티켓 하나씩 쿠폰번호 저장
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

        // 주문 존재 체크
        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        // 티켓 조회 (TNO + productCode)
        List<Map<String, String>> tickets =
                mapper.selectTicketsByOrderNumber(orderNumber);

        if (tickets == null || tickets.isEmpty()) {
            throw new RuntimeException("조회 가능한 티켓이 없습니다.");
        }

        boolean allUnused = true;
        boolean anyUsed = false;

        // 티켓 단위 조회
        for (Map<String, String> t : tickets) {

            String trno = t.get("trno");
            String productCode = t.get("productcode");

            log.info("[DEBUG] ticket map = {}", t);

            log.info("[MAIR][CHECK] TRNO={}, ITCD={}", trno, productCode);

            MairCouponResDto res = client.useCheck(productCode, trno);

            log.info("[MAIR][RESPONSE] {}", res);


            String code = res.getResult();

            log.info("[MAIR][RESULT] TRNO={}, code={}, msg={}",
                    trno,
                    code,
                    MairResultCode.message(code)
            );

            // 미사용
            if (MairResultCode.isUnused(code)) {
                continue;
            }

            allUnused = false;

            // 취소 / 발권 / 오류 → 상태만 로그
            if (MairResultCode.isCanceled(code)
                    || MairResultCode.isIssued(code)
                    || MairResultCode.isError(code)) {

                log.info("[MAIR][SKIP] trno={}, code={}", trno, code);
                continue;
            }

            // 사용됨
            if (MairResultCode.isUsed(code)) {

                anyUsed = true;

                // 중복 업데이트 방지 (이미 true면 skip)
                mapper.updateTicketUsedIfNotUsed(trno);

                log.info("[MAIR][USED UPDATE] tno={}", trno);
            }
        }

        //  결과 반환
        if (allUnused) {
            return new IntegrationResult(true, "OK", "전체 미사용");
        }

        if (anyUsed) {
            return new IntegrationResult(true, "USED", "사용된 티켓 존재");
        }

        return new IntegrationResult(true, "UNKNOWN", "상태 확인 필요");
    }

    // 휴대폰 번호 정규화
    private String normalizeHp(String hp) {
        return hp == null ? null : hp.replaceAll("[^0-9]", "");
    }
}
