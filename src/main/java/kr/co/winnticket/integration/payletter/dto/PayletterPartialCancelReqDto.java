package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "Payletter 부분취소 요청")
public class PayletterPartialCancelReqDto {
    @NotNull
    @JsonProperty("pgcode")
    @Schema(description = "결제수단 코드", example = "creditcard")
    private String pgCode;

    @NotNull
    @JsonProperty("client_id")
    @Schema(description = "가맹점 ID")
    private String clientId;

    @NotNull
    @JsonProperty("user_id")
    @Schema(description = "가맹점 결제자 ID")
    private String userId;

    @NotNull
    @Schema(description = "결제 고유 번호(tid)")
    private String tid;

    @Schema(description = "결제금액")
    private Integer amount;

    @JsonProperty("taxfree_amount")
    @Schema(description = "비과세금액")
    private Integer taxfreeAmount;

    @JsonProperty("tax_amount")
    @Schema(description = "부가세금액")
    private Integer taxAmount;

    @NotNull
    @JsonProperty("ip_addr")
    @Schema(description = "요청 IP")
    private String ipAddr;
}
