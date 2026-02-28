package kr.co.winnticket.integration.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntegrationResult {

    private final boolean success;
    private final String code;
    private final String message;

    public static IntegrationResult success() {
        return new IntegrationResult(true, "SUCCESS", "성공");
    }

    public static IntegrationResult fail(String code, String message) {
        return new IntegrationResult(false, code, message);
    }
}