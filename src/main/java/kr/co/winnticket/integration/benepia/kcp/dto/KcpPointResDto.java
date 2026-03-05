package kr.co.winnticket.integration.benepia.kcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[베네피아 KCP 포인트조회 응답 DTO]KcpPointResDto")
public class KcpPointResDto {

    @Schema(description = "결과코드")
    private String res_cd;

    @Schema(description = "결과메세지")
    private String res_msg;

    @Schema(description = "잔여포인트")
    private Integer rsv_pnt;

}
