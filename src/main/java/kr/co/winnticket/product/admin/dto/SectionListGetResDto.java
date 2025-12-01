package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[섹션 > 섹션 목록 조회] SectionListGetResDto")
public class SectionListGetResDto {
    @NotNull
    @Schema(description = "섹션_ID")
    private UUID id;

    @Schema(description = "섹션코드")
    private String code;

    @Schema(description = "섹션명")
    private String name;

    @Schema(description = "표시순서")
    private int displayOrder;

    @Schema(description = "활성화여부")
    private boolean isActive;

    @Schema(description = "시스템등록여부")
    private boolean isSystem;

    @Schema(description = "설명")
    private String description;
}
