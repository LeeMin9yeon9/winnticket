package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[LS컴퍼니 티켓 취소 응답 DTO] LsCancelResDto")
public class LsCancelResDto {
    @Schema(description = "결과 상태")
    private String status;

    @Schema(description = "결과 코드")
    private String resultCode;

    @Schema(description = "결과 메시지")
    private String resultMessage;

}
