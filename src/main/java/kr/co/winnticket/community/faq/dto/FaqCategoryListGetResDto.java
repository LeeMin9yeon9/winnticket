package kr.co.winnticket.community.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[FAQ > FAQ 카테고리 목록 조회] FaqCategoryListGetResDto")
public class FaqCategoryListGetResDto {
    @NotEmpty
    @Schema(description = "카테고리_ID")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "정렬순서")
    private int displayOrder;

    @Schema(description = "시스템등록여부")
    private boolean isSystem;
}
