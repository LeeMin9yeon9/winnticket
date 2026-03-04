package kr.co.winnticket.integration.benepia.kcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title="[KCP 포인트 취소 요청 DTO]KcpPointCancelReqDto")
public class KcpPointCancelReqDto {

    @NotNull
    @Schema(description = "KCP 거래번호")
    private String tno;

    @Schema(description = "주문번호")
    private String orderNo;

    @Schema(description = "취소 사유")
    private String cancelReason;
}
