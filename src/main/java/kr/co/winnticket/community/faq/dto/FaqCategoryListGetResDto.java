package kr.co.winnticket.community.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[FAQ > FAQ 카테고리 목록 조회] FaqCategoryListGetResDto")
public class FaqCategoryListGetResDto {
    @NotNull
    @Schema(description = "카테고리_ID")
    private UUID id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "정렬순서")
    private int displayOrder;
}
