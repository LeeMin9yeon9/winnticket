package kr.co.winnticket.community.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[FAQ > FAQ 카테고리 수정] FaqCategoryPatchReqDto")
public class FaqCategoryPatchReqDto {
    @NotEmpty
    @Schema(description = "이름")
    private String name;
}
