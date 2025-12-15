package kr.co.winnticket.channels.channelProducts.service;

import kr.co.winnticket.channels.channelProducts.dto.ChannelProductListResDto;
import kr.co.winnticket.channels.channelProducts.mapper.ChannelProductsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelProductService {

    private final ChannelProductsMapper mapper;

    // 채널 상품 목록 / 검색
    public List<ChannelProductListResDto> getChannelProducts(
            UUID channelId,
            String keyword,
            Boolean status
    ) {
        return mapper.selectChannelProducts(channelId, keyword, status);
    }
}
