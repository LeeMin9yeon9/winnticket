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


    // 배너 최대 displayOrder 조회
    Integer selectMaxDisplayOrder();

    // 배너 생성
    void insertBanner(BannerCreateDto dto);

    // 배너 수정
    void updateBanner(BannerUpdateDto dto);

    // 배너 상태
    void updateVisible(@Param("id") String id,
                       @Param("visible") Boolean visible);

    // 배너 소프트 삭제
    void softDelete(String id);

    // 배너 실제 삭제
    void hardDelete(String id);


    // 배너 상세
    BannerDto selectBannerById(@Param("id") String id);

    // SHOP 배너 조회 - channelId가 있으면 "전체 노출"(채널 제한 없음) 배너 + 해당 채널이
    // 지정된 배너만, channelId가 없으면 "전체 노출" 배너만 조회
    List<BannerDto> selectByPosition(@Param("position") String position,
                                      @Param("channelId") String channelId);

    // 배너에 연결된 채널 ID 목록 조회
    List<String> selectChannelIdsByBannerId(@Param("bannerId") String bannerId);

    // 배너-채널 연결 전체 삭제 (수정 시 재삽입 전 초기화용)
    void deleteBannerChannels(@Param("bannerId") String bannerId);

    // 배너-채널 연결 일괄 삽입
    void insertBannerChannels(@Param("bannerId") String bannerId,
                              @Param("channelIds") List<String> channelIds);
}

