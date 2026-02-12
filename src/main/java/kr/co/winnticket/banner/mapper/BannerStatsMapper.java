package kr.co.winnticket.banner.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BannerStatsMapper {

    // 배너 클릭 수 증가
    void increaseClickCount(@Param("bannerId") String bannerId);

    // 총 배너 클릭 수
    Long getTotalClickCount(@Param("bannerId") String bannerId);
}
