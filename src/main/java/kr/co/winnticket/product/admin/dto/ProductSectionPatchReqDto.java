package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 섹션정보 수정] ProductSectionPatchReqDto")
public class ProductSectionPatchReqDto {
    @NotNull
    @Schema(description = "섹션_ID")
    private UUID sectionId;

    @NotNull
    @Schema(description = "활성화여부")
    private boolean visible;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
