package kr.co.winnticket.integration.benepia.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[베네피아 공통 에러 정의] BenepiaErrorCode")
public enum BenepiaErrorCode {
    BENEPIA_DECRYPT_FAIL,
    BENEPIA_INVALID_PARAM,

}
