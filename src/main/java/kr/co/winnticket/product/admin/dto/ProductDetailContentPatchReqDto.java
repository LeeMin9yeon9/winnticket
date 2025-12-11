package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 상세내용 수정] ProductDetailContentPatchReqDto")
public class ProductDetailContentPatchReqDto {
    @Schema(description = "상세설명 HTML")
    private String detailContent;
    
    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
