package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[베네피아 포인트 → 이용권 전환 요청] PointVoucherExchangeReqDto")
public class PointVoucherExchangeReqDto {

    @NotBlank
    @Schema(description = "베네피아 아이디")
    private String benepiaId;

    @NotBlank
    @Schema(description = "베네피아 비밀번호")
    private String benepiaPwd;

    @NotBlank
    @Schema(description = "휴대폰번호 (사전에 인증 완료되어 있어야 함)")
    private String phone;

    @NotBlank
    @Schema(description = "주문자명")
    private String customerName;

    @NotNull
    @Min(1)
    @Schema(description = "전환할 포인트 금액")
    private Integer amount;

    @NotNull
    @Schema(description = "채널 ID")
    private UUID channelId;
}
