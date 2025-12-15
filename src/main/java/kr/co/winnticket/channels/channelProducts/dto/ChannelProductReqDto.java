package kr.co.winnticket.channels.channelProducts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[채널 > 상품관리 상품 제외 / 추가 ] ChannelProductReqDto")
public class ChannelProductReqDto {

    @Schema(description = "채널_ID")
    private UUID channelId;

    @Schema(description = "상품_ID")
    private UUID productId;

}
