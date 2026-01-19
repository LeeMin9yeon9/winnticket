package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[Payletter 결제요청 응답DTO] PayMentRequestResDto")
public class PayletterPaymentResDto {

    @Schema(description = "에러 코드")
    private Integer code;

    @Schema(description = "에러 메세지")
    private String message;

    @Schema(description = "Payletter 결제 토큰")
    private Long token;

    @JsonProperty("online_url")
    @Schema(description = "PC 결제 URL")
    private String onlineUrl;

    @JsonProperty("mobile_url")
    @Schema(description = "모바일 결제 URL")
    private String mobileUrl;

    public boolean isSuccess() {
        return token != null && (onlineUrl != null || mobileUrl != null);
    }
}