package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 관리자가 이용권을 수동으로 등록할 때 사용하는 요청 DTO.
 * 실제 KCP 포인트 차감 없이(point_tid 없음) 이용권 레코드만 직접 생성한다.
 */
@Data
@Schema(title = "[이용권 - 관리자] PointVoucherManualCreateReqDto")
public class PointVoucherManualCreateReqDto {

    @NotBlank
    @Schema(description = "이름")
    private String customerName;

    @NotBlank
    @Schema(description = "핸드폰번호")
    private String phone;

    @NotBlank
    @Schema(description = "베네피아 아이디")
    private String benepiaId;

    @NotNull
    @Schema(description = "발급일")
    private LocalDateTime validFrom;

    @NotNull
    @Schema(description = "사용기한")
    private LocalDateTime validUntil;

    @NotNull
    @Min(1)
    @Schema(description = "총금액")
    private Integer totalAmount;

    @NotNull
    @Min(0)
    @Schema(description = "사용금액 (이미 일부 사용된 상태로 등록해야 하는 경우)")
    private Integer usedAmount;

    @Schema(description = "채널 ID (선택)")
    private UUID channelId;

    @Schema(description = "소속사코드 (선택)")
    private String memcorpCd;
}
