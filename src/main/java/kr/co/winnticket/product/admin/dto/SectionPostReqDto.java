package kr.co.winnticket.product.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[섹션 > 섹션 등록] SectionPostReqDto")
public class SectionPostReqDto {
    @Schema(description = "섹션코드")
    private String code;

    @Schema(description = "섹션명")
    private String name;

    @Schema(description = "표시순서")
    private int displayOrder;

    @Schema(description = "설명")
    private String description;

    @JsonProperty("isActive")
    @Schema(description = "활성화여부")
    private boolean isActive;
}
