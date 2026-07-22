package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "[관리자 > 이용권 상세] PointVoucherAdminDetailResDto")
public class PointVoucherAdminDetailResDto extends PointVoucherAdminListResDto {

    @Schema(description = "취소 가능 여부 (미사용 + 취소가능기간 이내)")
    private boolean cancelable;

    @Schema(description = "취소 가능 기한 (채널에 취소가능기간이 설정된 경우만)")
    private java.time.LocalDateTime cancelDeadline;

    @Schema(description = "사용내역")
    private List<PointVoucherUsageResDto> usages;
}
