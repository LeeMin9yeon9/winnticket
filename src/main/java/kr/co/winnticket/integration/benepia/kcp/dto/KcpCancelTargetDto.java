package kr.co.winnticket.integration.benepia.kcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "[KCP 테스트계정 스케줄러 취소 DTO]KcpCancelTargetDto")
public class KcpCancelTargetDto {
    private String orderNo;
    private String tno;
}
