package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SIUseCallbackResponse {

    @JsonProperty("order_no")
    private final String orderNo;

    @JsonProperty("return_div")
    private final String returnDiv;

    @JsonProperty("return_msg")
    private final String returnMsg;

    public static SIUseCallbackResponse ok(String orderNo) {
        return new SIUseCallbackResponse(orderNo, "0000", "success");
    }

    public static SIUseCallbackResponse fail(String orderNo, String msg) {
        return new SIUseCallbackResponse(orderNo, "9999", msg);
    }
}