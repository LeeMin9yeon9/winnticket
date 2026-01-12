package kr.co.winnticket.integration.benepia.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "[베네피아 공통 예외] BenepiaException")
public class BenepiaException extends RuntimeException{
    private final BenepiaErrorCode errorCode;

    public BenepiaException(BenepiaErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BenepiaException(BenepiaErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BenepiaErrorCode getErrorCode() {
        return errorCode;
    }
}
