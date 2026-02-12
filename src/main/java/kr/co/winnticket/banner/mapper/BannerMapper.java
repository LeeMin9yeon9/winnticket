package kr.co.winnticket.banner.mapper;

import kr.co.winnticket.banner.dto.BannerCreateDto;
import kr.co.winnticket.banner.dto.BannerDto;
import kr.co.winnticket.banner.dto.BannerFilter;
import kr.co.winnticket.banner.dto.BannerUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BannerMapper {
    // 배너조회
    List<BannerDto>selectAdminList(BannerFilter filter);


    // 배너 생성
    void insertBanner(BannerCreateDto dto);

    // 배너 수정
    void updateBanner(BannerUpdateDto dto);

    // 배너 상태
    void updateVisible(@Param("id") String id,
                       @Param("visible") Boolean visible);

    // 배너 삭제
    void softDelete(String id);


    // 배너 상세
    BannerDto selectBannerById(@Param("id") String id);

    // SHOP 배너 조회
    List<BannerDto> selectByPosition(@Param("position") String position);
}

