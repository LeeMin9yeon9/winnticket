package kr.co.winnticket.order.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface PriceMapper {
    // 숙박 날짜 가격 합계
    Integer selectStayPrice(
            @Param("optionValueId") UUID optionValueId,
            @Param("dates") List<LocalDate> dates
    );

    // 옵션 가격
    Integer selectOptionPrice(
            @Param("productId") UUID productId,
            @Param("channelId") UUID channelId,
            @Param("optionValueIds") List<UUID> optionValueIds
    );

    // 채널별상품값 select
    Integer selectChannelProductPrice(
            @Param("productId") UUID productId,
            @Param("channelId") UUID channelId
    );

    // 상품값 select
    Integer selectProductPrice(
            @Param("productId") UUID productId
    );
}
