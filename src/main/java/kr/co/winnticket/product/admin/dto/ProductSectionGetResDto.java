package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[섹션 > 섹션 조회] ProductSectionGetResDto")
public class ProductSectionGetResDto {
    @NotNull
    @Schema(description = "섹션_ID")
    private UUID id;

    @Schema(description = "섹션명")
    private String name;

    @Schema(description = "섹션내용")
    private String description;

    @Schema(description = "활성화여부")
    private boolean visible;
}
