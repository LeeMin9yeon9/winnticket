package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 기간별 가격 목록] StayPeriodDto")
public class StayPeriodDto {
    @Schema(description = "그룹번호")
    private int groupNo;

    @Schema(description = "시작일")
    private LocalDate startDate;

    @Schema(description = "종료일")
    private LocalDate endDate;

    @Schema(description = "1박 가격")
    private int price;

    @Schema(description = "할인 1박 가격")
    private Integer discountPrice;
}
