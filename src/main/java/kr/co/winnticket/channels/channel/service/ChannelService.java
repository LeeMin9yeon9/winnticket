package kr.co.winnticket.channels.channel.service;

import kr.co.winnticket.channels.channel.dto.ChannelCreateReqDto;
import kr.co.winnticket.channels.channel.dto.ChannelInfoResGetDto;
import kr.co.winnticket.channels.channel.dto.ChannelListGetResDto;
import kr.co.winnticket.channels.channel.dto.ChannelPatchReqDto;
import kr.co.winnticket.channels.channel.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        if(mapper.existsCode(null,  model.getCode()) > 0){
            throw new IllegalArgumentException("이미 사용 중인 채널코드입니다.");
        }
        mapper.createChannel(model);
    }

    // 채널 기본정보 조회
    public ChannelInfoResGetDto selectChannel(UUID id) {
        return mapper.selectChannel(id);
    }

    // 채널 기본정보수정
    public void updateChannel(UUID id, ChannelPatchReqDto model){

        if(model.getCode() != null && !model.getCode().isEmpty()){
            if(mapper.existsCode(id, model.getCode()) > 0){
                throw new IllegalArgumentException("이미 다른 채널에서 사용 중인 코드입니다.");
            }
        }
        mapper.updateChannel(id,model);
    }

    // 채널 삭제
    public void deleteChannel(UUID id){
        mapper.deleteChannel(id);
    }

    // 채널 활성 / 비활성화
    public void visibleChannel(UUID id, Boolean visible){
        if(visible == null){
            throw new IllegalArgumentException("visible 값은 true 또는 false 여야합니다.");
        }
        mapper.visibleChannel(id,visible);
    }

    // 채널코드로 아이디찾기
    public UUID selectChannelIdByCode(String channelCode) {
        return mapper.selectChannelIdByCode(channelCode);
    }
}
