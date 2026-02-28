package kr.co.winnticket.integration.playstory.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.playstory.dto.PlaystoryCheckCancelResponse;
import kr.co.winnticket.integration.playstory.dto.PlaystoryCheckResponse;
import kr.co.winnticket.integration.playstory.dto.PlaystoryOrderResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaystoryResponseMapper {

    public IntegrationResult mapOrder(PlaystoryOrderResponse response) {

        if (response == null || response.getOptList() == null || response.getOptList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "응답이 없습니다.");
        }

        for (var opt : response.getOptList()) {

            String code = opt.getResultCode();

            if (!"1000".equals(code)) {
                return IntegrationResult.fail(
                        code,
                        opt.getResultMessage()
                );
            }
        }

        return IntegrationResult.success();
    }

    public IntegrationResult mapCheck(PlaystoryCheckResponse response) {

        if (response == null || response.getOptList() == null || response.getOptList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "조회 응답이 없습니다.");
        }

        for (var opt : response.getOptList()) {

            String code = opt.getResultCode();

            if (!isCheckSuccess(code)) {
                return IntegrationResult.fail(
                        code,
                        opt.getResultMessage()
                );
            }
        }

        return IntegrationResult.success();
    }

    public IntegrationResult mapCancel(PlaystoryCheckCancelResponse response) {

        if (response == null || response.getOptList() == null || response.getOptList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "취소 응답이 없습니다.");
        }

        for (var opt : response.getOptList()) {

            String code = opt.getResultCode();

            if (!"3000".equals(code)) {
                return IntegrationResult.fail(
                        code,
                        opt.getResultMessage()
                );
            }
        }

        return IntegrationResult.success();
    }

    private boolean isCheckSuccess(String code) {
        return "2000".equals(code)
                || "2001".equals(code)
                || "2002".equals(code);
    }
}
