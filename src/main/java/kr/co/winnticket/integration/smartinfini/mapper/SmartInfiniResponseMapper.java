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
}