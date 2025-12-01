package kr.co.winnticket.partner.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[파트너 > 파트너 목록 ] PartnerListGetResDto")
public class PartnerListGetResDto {
    @Hidden
    @Schema(description = "파트너 ID")
    private UUID id;

    @Schema(description = "파트너 코드")
    private String code;

    @Schema(description = "파트너명")
    private String name;

    @Schema(description = "파트너 타입")
    private PartnerType type;

    @Schema(description = "담당자명")
    private String managerName;

    @Schema(description = "상태")
    private PartnerStatus status;

    @Schema(description = "계약 시작일")
    private LocalDateTime contractStartDate;

    @Schema(description = "계약 종료일")
    private LocalDateTime contractEndDate;

    @Schema(description = "수수표율 %")
    private Integer commissionRate;

    @Schema(description = "활성화여부")
    private boolean visible;




}
