package kr.co.winnticket.integration.plusn.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.plusn.dto.*;
import org.springframework.stereotype.Component;
@Component
public class PlusNResponseMapper {

    // 주문 성공 여부
    public IntegrationResult mapOrder(PlusNOrderResponse res) {

        if (res == null) {
            return IntegrationResult.fail("EMPTY", "응답 없음");
        }

        if (!"0000".equals(res.getReturn_div())) {
            return IntegrationResult.fail(
                    res.getReturn_div(),
                    res.getReturn_msg()
            );
        }

        return IntegrationResult.success();
    }

    // 취소 가능 여부 (조회)
    public IntegrationResult mapInquiry(PlusNInquiryResponse res) {

        if (res == null) {
            return IntegrationResult.fail("EMPTY", "조회 응답 없음");
        }

        if (!"0005".equals(res.getReturn_div())) {
            return IntegrationResult.fail(
                    res.getReturn_div(),
                    res.getReturn_msg()
            );
        }

        return IntegrationResult.success();
    }

    // 날짜별 사용조회
    public IntegrationResult mapUsedDate(PlusNUsedDateResponse res) {

        if (res == null) {
            return IntegrationResult.fail("NULL", "PlusN 사용조회 응답 없음");
        }

        if (!"0000".equals(res.getReturn_div())) {
            return IntegrationResult.fail(
                    res.getReturn_div(),
                    res.getReturn_msg()
            );
        }

        return IntegrationResult.success();
    }

    // 취소 성공 여부
    public IntegrationResult mapCancel(PlusNCancelResponse res) {

        if (res == null) {
            return IntegrationResult.fail("EMPTY", "취소 응답 없음");
        }

        if (!"0000".equals(res.getReturn_div())) {
            return IntegrationResult.fail(
                    res.getReturn_div(),
                    res.getReturn_msg()
            );
        }

        return IntegrationResult.success();
    }
}