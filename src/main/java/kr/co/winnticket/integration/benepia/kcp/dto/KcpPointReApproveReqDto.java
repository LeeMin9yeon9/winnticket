package kr.co.winnticket.integration.benepia.kcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[KCP 포인트 재승인 요청 DTO]KcpPointReApproveReqDto")
public class KcpPointReApproveReqDto {
    @Schema(description = "기존 거래번호(TNO)", example = "23030112345678901234")
    private String oldTno;

    @Schema(description = "새 주문번호", example = "ORD20260303002")
    private String newOrderNo;

    @Schema(description = "재승인 금액", example = "10000")
    private int amount;

    @Schema(description = "상품명", example = "워터파크 입장권")
    private String goodsName;
}
