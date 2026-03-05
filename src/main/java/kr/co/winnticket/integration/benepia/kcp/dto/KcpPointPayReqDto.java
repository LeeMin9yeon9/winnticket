package kr.co.winnticket.integration.benepia.kcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[베네피아 KCP 포인트결제 요청 DTO]KcpPointPayReqDto")
public class KcpPointPayReqDto {

    @NotNull
    @Schema(description = "주문번호")
    private String orderNo;

    @NotNull
    @Min(1)
    @Schema(description = "결제금액")
    private Integer amount;

    @NotNull
    @Schema(description = "베네피아 아이디",example="testtravel")
    private String benepiaId;

    @NotNull
    @Schema(description = "베네피아 비밀번호",example = "skmns@9955")
    private String benepiaPwd;

    @NotNull
    @Schema(description = "회원 소속사 코드", example = "TEST 시 5555")
    private String memcorpCd;

    @NotNull
    @Schema(description = "상품명")
    private String productName;

    @NotNull
    @Schema(description = "상품코드")
    private String productCode;

    @NotNull
    @Schema(description = "주문자명")
    private String buyerName;

    @Schema(description = "주문자 휴대폰")
    private String buyerPhone;

    @Schema(description = "주문자 이메일")
    private String buyerEmail;
}
