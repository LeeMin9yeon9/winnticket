package kr.co.winnticket.integration.smartinfini.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.smartinfini.dto.SICancelListResponse;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class SmartInfiniResponseMapper {

    // 일반 응답용
    public IntegrationResult map(String returnDiv, String returnMsg) {

        if (returnDiv == null) {
            return IntegrationResult.fail("EMPTY", "응답 없음");
        }

        if (!"0000".equals(returnDiv)) {
            return IntegrationResult.fail(returnDiv, returnMsg);
        }

        return IntegrationResult.success();
    }

    // 조회
    public IntegrationResult mapSearch(SIOrderSearchResponse res) {

        if (res == null || res.getInquiryList() == null || res.getInquiryList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "조회 결과 없음");
        }

        for (SIOrderSearchResponse.InquiryItem item : res.getInquiryList()) {

            if (!"0000".equals(item.getReturnDiv())) {

                return IntegrationResult.fail(
                        item.getReturnDiv(),
                        item.getReturnMsg()
                );
            }
        }

        return IntegrationResult.success();
    }

    // 취소
    public IntegrationResult mapCancelList(SICancelListResponse res) {

        if (res == null || res.getCancelList() == null || res.getCancelList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "취소 응답 없음");
        }

        for (SICancelListResponse.CancelResult item : res.getCancelList()) {

            if (!"0000".equals(item.getReturnDiv())) {

                return IntegrationResult.fail(
                        item.getReturnDiv(),
                        item.getReturnMsg()
                );
            }
        }

        return IntegrationResult.success();
    }
}