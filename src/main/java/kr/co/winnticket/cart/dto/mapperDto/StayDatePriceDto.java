package kr.co.winnticket.cart.dto.mapperDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
@Schema(title = "[ SHOP > MAPPER 숙박 기간 조회] StayDatePriceDto")
public class StayDatePriceDto {

    @Schema(description = "숙박기간id")
    private UUID id;

    @Schema(description = "숙박기간옵션id")
    private UUID optionValueId;


    @Schema(description = "숙박상품날짜")
    private LocalDate priceDate;

    @Schema(description = "숙박상품 가격")
    private int price;

    @Schema(description = "숙박상품 할인가")
    private Integer discountPrice;

    @Schema(description = "티켓그룹")
    private Integer groupNo;


}
