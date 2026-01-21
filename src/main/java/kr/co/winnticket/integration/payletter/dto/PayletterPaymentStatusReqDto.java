package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[Payletter 거래상태조회 요청 DTO] PayletterPaymentStatusReqDto")
public class PayletterPaymentStatusReqDto {

    @NotNull
    @Schema(description = "가맹점 아이디")
    @JsonProperty("client_id")
    private String clientId;

    @NotNull
    @Schema(description = "가맹점 주문번호")
    @JsonProperty("order_no")
    private String orderNo;
}
