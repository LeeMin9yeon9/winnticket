package kr.co.winnticket.integration.benepia.kcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title="[KCP 포인트 취소 요청 DTO]KcpPointCancelReqDto")
public class KcpPointCancelReqDto {

    @NotNull
    @Schema(description = "KCP 거래번호")
    private String tno;

    @Schema(description = "주문번호")
    private String orderNo;

    @Schema(description = "취소 사유")
    private String cancelReason;

    @Schema(description = "STSC / STRA")
    private String modType;

    @Schema(description = "STRA 부분취소 시 '취소 후 남는 금액(잔액)' — 환불액이 아님")
    private Integer modMny;

    @Schema(description = "부분취소  주문번호")
    private String modOrdrIdxx;

    @Schema(description = "상품명")
    private String modOrdrGoods;
}
