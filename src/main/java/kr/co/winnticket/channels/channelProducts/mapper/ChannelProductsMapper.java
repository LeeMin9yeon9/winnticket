package kr.co.winnticket.channels.channelProducts.mapper;

import kr.co.winnticket.channels.channelProducts.dto.ChannelProductListResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ChannelProductsMapper {

    // 채널 상품 목록 조회
    List<ChannelProductListResDto> selectChannelProducts(
            @Param("channelId") UUID channelId,
            @Param("keyword") String keyword,
            @Param("exclude") Boolean exclude
    );

    // 상품 제외
    void excludeChannelProduct(
            @Param("channelId") UUID channelId,
            @Param("productId") UUID productId
    );

    // 상품 복구
    void includeChannelProduct(
            @Param("channelId") UUID channelId,
            @Param("productId") UUID productId
    );
}
