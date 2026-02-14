package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 가격 목록 조회] ProductChannelPriceListResDto")
public class ProductChannelPriceListResDto {
    @Schema(description = "채널로고url")
    private String logoUrl;

    @Schema(description = "채널_ID")
    private UUID channelId;

    @Schema(description = "채널명")
    private String channelName;

    @Schema(description = "운영회사명")
    private String companyName;

    @Schema(description = "채널코드")
    private String channelCode;

    @Schema(description = "기본가")
    private int basePrice;

    @Schema(description = "할인가")
    private int discountPrice;

    @Schema(description = "활성화여부")
    private boolean enabled;
}
