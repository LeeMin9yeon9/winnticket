package kr.co.winnticket.integration.spavis.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class SpavisResponseMapper {

    public IntegrationResult mapCheck(SPCouponCheckResponse res) {

        if (res == null) {
            return IntegrationResult.fail("EMPTY", "응답 없음");
        }

        if ("S".equals(res.getRtnDiv())) {
            return IntegrationResult.success();
        }

        return IntegrationResult.fail(
                res.getRtnDiv(),
                res.getRtnMsg()
        );
    }
}
