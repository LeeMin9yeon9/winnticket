package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[LS컴퍼니 티켓 사용 요청 DTO] LsTicketUseReqDto")
public class LsTicketUseReqDto {
    @Schema(description = "파트너 주문번호")
    private String orderNo;

    @Schema(description = "파트너 티켓고유번호")
    private String transactionId;

    @Schema(description = "상태변경코드")
    private String code;

    @Schema(description = "변경일자")
    private String date;
}
