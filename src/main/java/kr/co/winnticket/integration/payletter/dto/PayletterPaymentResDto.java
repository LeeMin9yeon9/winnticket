package kr.co.winnticket.integration.payletter.dto;

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

    @Schema(description = "PC 결제 URL")
    private String onlineUrl;

    @Schema(description = "모바일 결제 URL")
    private String mobileUrl;

    public boolean isSuccess() {
        return token != null && (onlineUrl != null || mobileUrl != null);
    }
}