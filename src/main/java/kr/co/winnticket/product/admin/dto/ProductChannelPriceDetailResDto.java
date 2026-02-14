package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 가격 상세 조회] ProductChannelPriceDetailResDto")
public class ProductChannelPriceDetailResDto {
    @NotNull
    @Schema(description = "채널_ID")
    private UUID channelId;

    @Schema(description = "채널로고url")
    private String logoUrl;

    @Schema(description = "운영회사명")
    private String companyName;

    @Schema(description = "옵션별 가격")
    private List<ProductChannelOptionPriceGetResDto> options = new ArrayList<>();
}
