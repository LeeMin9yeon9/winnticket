package kr.co.winnticket.integration.woongjin.mapper;

import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.woongjin.dto.*;
import org.springframework.stereotype.Component;

@Component
public class WoongjinResponseMapper {

    private boolean isSuccessCode(String code) {
        return "S000".equals(code) || "S001".equals(code);
    }

    // =========================
    // 주문
    // =========================
    public ApiResponse<WJOrderResponse> mapOrder(WJOrderResponse res) {

        if (res == null) {
            return ApiResponse.error("웅진 주문 응답 없음", "WOONGJIN_NO_RESPONSE");
        }

        if (!isSuccessCode(res.getCode())) {
            return ApiResponse.error(res.getMessage(), res.getCode());
        }

        return ApiResponse.success("웅진 주문 성공", res);
    }

    // =========================
    // 조회
    // =========================
    public ApiResponse<WJOrderInquiryResponse> mapInquiry(WJOrderInquiryResponse res) {

        if (res == null) {
            return ApiResponse.error("웅진 조회 응답 없음", "WOONGJIN_NO_RESPONSE");
        }

        if (!isSuccessCode(res.getCode())) {
            return ApiResponse.error(res.getMessage(), res.getCode());
        }

        return ApiResponse.success("웅진 조회 성공", res);
    }

    // =========================
    // 취소
    // =========================
    public ApiResponse<WJCancelResponse> mapCancel(WJCancelResponse res) {

        if (res == null) {
            return ApiResponse.error("웅진 취소 응답 없음", "WOONGJIN_NO_RESPONSE");
        }

        if (!isSuccessCode(res.getCode())) {
            return ApiResponse.error(res.getMessage(), res.getCode());
        }

        return ApiResponse.success("웅진 취소 성공", res);
    }

    // =========================
    // 핀 재전송
    // =========================
    public ApiResponse<WJResendResponse> mapResend(WJResendResponse res) {

        if (res == null) {
            return ApiResponse.error("웅진 재전송 응답 없음", "WOONGJIN_NO_RESPONSE");
        }

        if (!isSuccessCode(res.getCode())) {
            return ApiResponse.error(res.getMessage(), res.getCode());
        }

        return ApiResponse.success("웅진 재전송 성공", res);
    }
}