package kr.co.winnticket.channels.channelProducts.mapper;

import kr.co.winnticket.channels.channelProducts.dto.ChannelProductListResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ChannelProductsMapper {

    List<ChannelProductListResDto> selectChannelProducts(
            @Param("channelId") UUID channelId,
            @Param("keyword") String keyword,
            @Param("status") Boolean status
    );
}
