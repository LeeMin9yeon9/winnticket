package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(title = "[LS컴퍼니 티켓 사용 응답 DTO] LsTicketUseResDto")
public class LsTicketUseResDto {
    @Schema(description = "결과 상태")
    private String status;

    @Schema(description = "결과 코드")
    private String resultCode;

    @Schema(description = "결과 메시지")
    private String resultMessage;
}
