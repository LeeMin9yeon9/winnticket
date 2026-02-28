package kr.co.winnticket.integration.coreworks.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.coreworks.dto.CWCancelResponse;
import kr.co.winnticket.integration.coreworks.dto.CWSearchResponse;
import kr.co.winnticket.integration.coreworks.dto.CWUseSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class CoreWorksResponseMapper {

    // 주문 (201이면 이미 성공이므로 body 검증 불필요)
    public IntegrationResult mapOrder(int httpStatus) {

        if (httpStatus == 201) {
            return IntegrationResult.success();
        }

        return IntegrationResult.fail(
                String.valueOf(httpStatus),
                "코어웍스 주문 실패"
        );
    }

    // 조회
    public IntegrationResult mapSearch(CWSearchResponse res) {
        if (res == null || res.getPinList() == null || res.getPinList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "코어웍스 조회 결과 없음");
        }

        for (CWSearchResponse.Pin pin : res.getPinList()) {
            String code = pin.getCode();

            if (!isSearchSuccess(code)) {
                return IntegrationResult.fail(
                        code,
                        "코어웍스 조회 실패"
                );
            }
        }

        return IntegrationResult.success();
    }

    private boolean isSearchSuccess(String code) {
        return "0".equals(code) || "1".equals(code) || "2".equals(code);
    }

    // 취소
    public IntegrationResult mapCancel(CWCancelResponse res) {

        if (res == null || res.getPinList() == null || res.getPinList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "코어웍스 취소 응답 없음");
        }

        for (CWCancelResponse.Pin pin : res.getPinList()) {

            String code = pin.getCode();

            if (!"0".equals(code)) {
                return IntegrationResult.fail(
                        code,
                        "코어웍스 취소 실패"
                );
            }
        }

        return IntegrationResult.success();
    }

    // 사용조회
    public IntegrationResult mapUseSearch(CWUseSearchResponse res) {
        if (res == null || res.getPinList() == null || res.getPinList().isEmpty()) {
            return IntegrationResult.fail("EMPTY", "코어웍스 사용조회 응답 없음");
        }

        for (CWUseSearchResponse.Pin pin : res.getPinList()) {
            String code = pin.getCode();

            if (!isUseSearchSuccess(code)) {
                return IntegrationResult.fail(
                        code,
                        "코어웍스 사용조회 실패"
                );
            }
        }

        return IntegrationResult.success();
    }

    private boolean isUseSearchSuccess(String code) {
        return "0".equals(code) || "2".equals(code);
    }

    //  재발송
    public IntegrationResult mapMmsResend(int httpStatus) {

        if (httpStatus == 200) {
            return IntegrationResult.success();
        }

        return IntegrationResult.fail(
                String.valueOf(httpStatus),
                "코어웍스 재발송 실패"
        );
    }
}