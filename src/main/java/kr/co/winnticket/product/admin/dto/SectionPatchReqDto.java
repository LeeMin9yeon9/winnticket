package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[섹션 > 섹션 수정] SectionPatchReqDto")
public class SectionPatchReqDto {
    @Schema(description = "섹션코드")
    private String code;

    @Schema(description = "섹션명")
    private String name;

    @Schema(description = "표시순서")
    private Integer displayOrder;

    @Schema(description = "설명")
    private String description;

    @Schema(description = "활성화여부")
    private boolean isActive;
}
