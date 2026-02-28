package kr.co.winnticket.integration.plusn.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.plusn.client.PlusNClient;
import kr.co.winnticket.integration.plusn.dto.*;
import kr.co.winnticket.integration.plusn.mapper.PlusNMapper;
import kr.co.winnticket.integration.plusn.mapper.PlusNResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlusNService {

    private final PlusNClient client;
    private final PlusNMapper mapper;
    private final PlusNResponseMapper responseMapper;

    // =========================
    // 주문
    // =========================
    public PlusNOrderResponse order(UUID orderId) {

        PlusNOrderRequest req =
                mapper.selectPlusNOrderBase(orderId);

        PlusNOrderResponse res =
                client.order(req);

        validate(
                responseMapper.mapOrder(res),
                "PlusN 주문 실패"
        );

        return res;
    }

    // =========================
    // 전체 취소 (하나라도 실패 시 전체 실패)
    // =========================
    @Transactional
    public PlusNBatchCancelResponse cancel(UUID orderId) {

        log.info("[PlusN] cancel start orderId={}", orderId);

        List<PlusNCancelRequest> tickets =
                mapper.selectPlusNCancel(orderId);

        if (tickets == null || tickets.isEmpty()) {
            log.warn("[PlusN] 취소 대상 없음 orderId={}", orderId);
            return PlusNBatchCancelResponse.fail("취소 대상이 없습니다.");
        }

        // 전체 취소 가능 여부 사전 검증
        for (PlusNCancelRequest req : tickets) {

            PlusNInquiryRequest inquiry = new PlusNInquiryRequest();
            inquiry.setOrder_id(req.getOrder_id());
            inquiry.setOrder_sales(req.getOrder_sales());

            PlusNInquiryResponse inquiryRes = client.inquiry(inquiry);
            var inquiryResult = responseMapper.mapInquiry(inquiryRes);

            if (!inquiryResult.isSuccess()) {

                log.error("[PlusN] 취소 불가 orderId={}, message={}",
                        orderId, inquiryResult.getMessage());

                return PlusNBatchCancelResponse.fail(
                        "취소 불가: " + inquiryResult.getMessage()
                );
            }
        }

        // 전부 취소 실행
        List<String> canceledTickets = new ArrayList<>();

        for (PlusNCancelRequest req : tickets) {

            PlusNCancelResponse cancelRes = client.cancel(req);
            var cancelResult = responseMapper.mapCancel(cancelRes);

            if (!cancelResult.isSuccess()) {

                log.error("[PlusN] 취소 실패 orderId={}, message={}",
                        orderId, cancelResult.getMessage());

                return PlusNBatchCancelResponse.fail(
                        "취소 실패: " + cancelResult.getMessage()
                );
            }

            canceledTickets.add(req.getOrder_sales());
        }

        log.info("[PlusN] cancel success orderId={}, count={}",
                orderId, canceledTickets.size());

        return PlusNBatchCancelResponse.success(canceledTickets);
    }

    // =========================
    // 날짜별 사용조회
    // =========================
    public PlusNUsedDateResponse usedDate(String yyyymmdd) {

        PlusNUsedDateRequest req =
                new PlusNUsedDateRequest();

        req.setOrder_date(yyyymmdd);

        PlusNUsedDateResponse res =
                client.usedDate(req);

        validate(
                responseMapper.mapUsedDate(res),
                "PlusN 사용조회 실패"
        );

        return res;
    }

    // =========================
    // 공통 검증
    // =========================
    private void validate(IntegrationResult result,
                          String defaultMessage) {

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    defaultMessage
                            + " - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }
    }
}