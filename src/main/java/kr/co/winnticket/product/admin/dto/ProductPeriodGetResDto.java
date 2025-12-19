package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 기간 조회] ProductPeriodGetResDto")
public class ProductPeriodGetResDto {
    @Schema(description = "기간 ID")
    private UUID periodId;

    @Schema(description = "옵션 ID")
    private UUID optionId;

    @Schema(description = "옵션명 (예: 객실 타입)")
    private String optionName;

    @Schema(description = "옵션 값 ID")
    private UUID optionValueId;

    @Schema(description = "옵션값명 (예: 디럭스 룸)")
    private String optionValueName;

    @Schema(description = "기간별 가격 목록")
    private List<StayPeriodDto> periods = new ArrayList<>();
}
