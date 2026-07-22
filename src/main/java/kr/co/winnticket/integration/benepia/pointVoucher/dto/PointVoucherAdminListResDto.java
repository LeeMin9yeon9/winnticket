package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[관리자 > 이용권 목록] PointVoucherAdminListResDto")
public class PointVoucherAdminListResDto {

    @Schema(description = "이용권 ID")
    private UUID id;

    @Schema(description = "이용권 번호")
    private String voucherNumber;

    @Schema(description = "베네피아 아이디")
    private String benepiaId;

    @Schema(description = "이름")
    private String customerName;

    @Schema(description = "휴대폰번호")
    private String phone;

    @Schema(description = "총금액")
    private Integer totalAmount;

    @Schema(description = "사용금액")
    private Integer usedAmount;

    @Schema(description = "잔여금액")
    private Integer remainingAmount;

    @Schema(description = "발급일시")
    private LocalDateTime validFrom;

    @Schema(description = "사용기한")
    private LocalDateTime validUntil;

    @Schema(description = "상태")
    private String status;
}
