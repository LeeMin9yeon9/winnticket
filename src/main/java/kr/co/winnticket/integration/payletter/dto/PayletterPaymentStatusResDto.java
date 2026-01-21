package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[Payletter 거래상태조회 응답 DTO] PayletterPaymentStatusResDto")
public class PayletterPaymentStatusResDto {

    @Schema(description = "결과")
    private Integer code;

    @Schema(description = "메세지")
    private String message;

    @Schema(description = "가맹점 아이디")
    @JsonProperty("client_id")
    private String clientId;

    @Schema(description = "가맹점 주문번호")
    @JsonProperty("order_no")
    private String orderNo;

    @Schema(description = "결제 토큰")
    private String token;

    @Schema(description = "결제고유번호")
    private String tid;

    @Schema(description = "상태코드 (1:생성,2:진입,3:인증,4:실패,5:완료)")
    @JsonProperty("status_code")
    private Integer statusCode;

    public boolean isSuccess() {
        return code != null && code == 0;
    }
}