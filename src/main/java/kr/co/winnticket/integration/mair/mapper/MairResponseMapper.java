package kr.co.winnticket.integration.mair.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import org.springframework.stereotype.Component;

@Component
public class MairResponseMapper {

    // 쿠폰 발송 요청
    public IntegrationResult mapIssue(MairCouponResDto res) {
        if (res == null) {
            return IntegrationResult.fail("NULL", "엠에어 응답 없음");
        }

        if (!res.isOk()) {
            return IntegrationResult.fail(
                    res.getResult(),
                    "엠에어 발송 실패"
            );
        }

        return IntegrationResult.success();
    }

    // 취소
    public IntegrationResult mapCancel(MairCouponResDto res) {
        if (res == null) {
            return IntegrationResult.fail("NULL", "엠에어 응답 없음");
        }

        if (!"OK".equalsIgnoreCase(res.getResult())) {
            return IntegrationResult.fail(
                    res.getResult(),
                    "엠에어 취소 실패"
            );
        }

        return IntegrationResult.success();
    }

    // 사용여부 확인
    public IntegrationResult mapUseCheck(MairCouponResDto res) {
        if (res == null) {
            return IntegrationResult.fail("NULL", "엠에어 사용조회 응답 없음");
        }

        if (!"OK".equalsIgnoreCase(res.getResult())) {
            return IntegrationResult.fail(
                    res.getResult(),
                    "엠에어 사용조회 실패"
            );
        }

        return IntegrationResult.success();
    }
}
