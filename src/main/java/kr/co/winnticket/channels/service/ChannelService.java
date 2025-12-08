package kr.co.winnticket.channels.service;

import kr.co.winnticket.channels.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.dto.ChannelListGetResDto;
import kr.co.winnticket.channels.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelMapper mapper;

    // 채널조회 + 검색
    public List<ChannelListGetResDto> getChannelList(String code, String name, String companyName){
        return mapper.selectPartnerList(code,name,companyName);
    }

    // 채널 생성
    public void createChannel(ChannelCreateReqDto model){
        mapper.createChannel(model);

    }
}
