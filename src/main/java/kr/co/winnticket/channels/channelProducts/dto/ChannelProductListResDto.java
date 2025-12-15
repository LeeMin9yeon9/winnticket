package kr.co.winnticket.channels.channelProducts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[채널 > 상품관리 상품리스트 ] ChannelProductListResDto")
public class ChannelProductListResDto {

    @Schema(description = "상품_ID")
    private UUID id;

    @Schema(description = "상품코드")
    private String productCode;

    @Schema(description = "상품이름")
    private String productName;

    @Schema(description = "로고이미지")
    private String logoUrl;

    @Schema(description = "상품 제외 상태 (true=제외된것)")
    private Boolean exclude;

}
