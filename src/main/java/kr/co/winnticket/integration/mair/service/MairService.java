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

import static org.apache.commons.compress.harmony.pack200.PackingUtils.log;

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
        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if(order == null){
            log("[MAIR] 주문이 존재하지 않습니다.");
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        // 결제 완료 주문만 발송
        if(!"PAID".equals(order.getPaymentStatus()) && !"COMPLETED".equals(order.getOrderStatus())){
            log("[MAIR] 결제 완료 주문만 쿠폰 발송 가능합니다.");
            throw new IllegalStateException("결제 완료 주문만 쿠폰 발송 가능합니다.");
        }

        // 주문상품 조회
        List<MairOrderItemInfoDto> items = mapper.selectOrderItemInfos(order.getOrderId());

        List<MairCouponResDto> results = new ArrayList<>();

        for (MairOrderItemInfoDto item : items) {
            if (item.getProductCode() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }

            int issued = mapper.countOrderTickets(item.getOrderItemId());
            int need = item.getQuantity() - issued;

            for (int i = 0; i < need; i++) {
                MairCouponResDto res = client.issue(
                        item.getProductCode(),
                        orderNumber,
                        order.getCustomerName(),
                        normalizeHp(order.getCustomerPhone())
                );

                IntegrationResult result = responseMapper.mapIssue(res);

                log("[MAIR] 발급로그 result = " + result);

                if (!result.isSuccess()) {
                    throw new RuntimeException(
                            "엠에어 발송 실패 - code: "
                                    + result.getCode()
                                    + ", message: "
                                    + result.getMessage()
                    );
                }

                results.add(res);
            }
        }

        return results;
    }

    // 취소
    public void cancelByOrder(String orderNumber) {
        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if (order == null) {
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        // 주문 상품 조회
        List<MairOrderItemInfoDto> items =
                mapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {
            if (item.getProductCode() == null) {
                continue;
            }

            // TRNO = 주문번호 사용
            String trno = orderNumber;

            log.info("[MAIR] 취소 요청 ITCD={}, TRNO={}", item.getProductCode(), trno);

            MairCouponResDto res = client.cancel(item.getProductCode(), trno);

            IntegrationResult result = responseMapper.mapCancel(res);

            if (!result.isSuccess()) {
                throw new RuntimeException(
                        "엠에어 취소 실패 code="
                                + result.getCode()
                                + " message="
                                + result.getMessage()
                );
            }
        }
    }

    // 사용여부 확인
    public void useCheckByOrderNumber(String orderNumber) {
        MairOrderInfoDto order = mapper.selectOrderInfo(orderNumber);

        if (order == null) {
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        List<MairOrderItemInfoDto> items = mapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {

            if (item.getProductCode() == null
                    || item.getProductCode().isBlank()) {
                continue;
            }

            MairCouponResDto res = client.useCheck(item.getProductCode(), orderNumber);
            IntegrationResult result = responseMapper.mapUseCheck(res);

            if (!result.isSuccess()) {
                throw new RuntimeException(
                        "엠에어 사용조회 실패 - code: "
                                + result.getCode()
                                + ", message: "
                                + result.getMessage()
                );
            }
        }
    }

    // 휴대폰 번호 정규화
    private String normalizeHp(String hp) {
        return hp == null ? null : hp.replaceAll("[^0-9]", "");
    }
}
