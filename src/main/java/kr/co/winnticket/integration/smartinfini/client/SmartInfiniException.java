package kr.co.winnticket.integration.smartinfini.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SmartInfiniException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String rawBody;

    public SmartInfiniException(String message, HttpStatus httpStatus, String rawBody, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.rawBody = rawBody;
    }

    public SmartInfiniException(String message, HttpStatus httpStatus, String rawBody) {
        super(message);
        this.httpStatus = httpStatus;
        this.rawBody = rawBody;
    }
}