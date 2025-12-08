package kr.co.winnticket.channels.mapper;

import kr.co.winnticket.channels.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.dto.ChannelListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChannelMapper {

    // 채널조회 + 검색
    List<ChannelListGetResDto> selectPartnerList(
            @Param("code") String code,
            @Param("name") String name,
            @Param("companyName") String companyName
    );

    // 채널 등록
    void createChannel(ChannelCreateReqDto model);
}
