package kr.co.winnticket.partners.partnerinfo.dto;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(title = "[파트너 > 파트너 수정 DTO ] PartnerPatchResDto")
public class PartnerPatchResDto {

    @Hidden
    @Schema(description = "파트너 ID")
    private UUID id;

    @NotEmpty
    @Schema(description = "파트너 코드")
    private String code;

    @Schema(description = "파트너명")
    private String name;

    @Schema(description = "파트너타입")
    private PartnerType type;

    @Schema(description = "상태")
    private PartnerStatus status;

    @Schema(description = "사업자번호")
    private String businessNumber;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "담당자명")
    private String managerName;

    @Schema(description = "담당자 이메일")
    private String managerEmail;

    @Schema(description = "담당자 전화번호")
    private String managerPhone;

    @Schema(description = "계약 시작일")
    private LocalDate contractStartDate;

    @Schema(description = "계약 종료일")
    private LocalDate contractEndDate;

    @Schema(description = "수수료율")
    private Integer commissionRate;

    @Schema(description = "로고URL")
    private String logoUrl;

    @Schema(description = "쿠폰코드 생성 여부")
    private boolean couponCode;

    @Schema(description = "파트너 설명")
    private String description;
}
