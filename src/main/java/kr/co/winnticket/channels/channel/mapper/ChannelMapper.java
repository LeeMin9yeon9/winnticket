package kr.co.winnticket.channels.channel.mapper;

import kr.co.winnticket.channels.channel.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.channel.dto.ChannelInfoResGetDto;
import kr.co.winnticket.channels.channel.dto.ChannelListGetResDto;
import kr.co.winnticket.channels.channel.dto.ChannelPatchReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ChannelMapper {

    // 채널 코드로 상세조회
    ChannelInfoResGetDto selectChannelByCode(@Param("code") String code);

    // 채널조회 + 검색
    List<ChannelListGetResDto> selectPartnerList(
            @Param("code") String code,
            @Param("name") String name,
            @Param("companyName") String companyName
    );

    // 채널 등록
    void createChannel(ChannelCreateReqDto model);

    // 채널 상세조회
    ChannelInfoResGetDto selectChannel(@Param("id") UUID id);

    // 채널 기본정보 수정
    void updateChannel(@Param("id") UUID id,
                       @Param("model") ChannelPatchReqDto model);

    // 채널 삭제
    void deleteChannel(@Param("id") UUID id);

    // 채널 중복 체크
    int existsCode(@Param("id") UUID id, @Param("code") String code);

    // 채널 활성/비활성화
    void visibleChannel(@Param("id") UUID id,
                        @Param("visible")Boolean visible);

    // 채널코드로 id 찾기
    UUID selectChannelIdByCode(String channelCode);

    // 채널id로 code찾기
    Boolean selectUseCardById(@Param("id") UUID id);

}
