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
            Boolean exclude
    ) {
        return mapper.selectChannelProducts(channelId, keyword, exclude);
    }

    // 채널 상품 제외
    public void excludeProduct(UUID channelId, UUID productId){
        mapper.excludeChannelProduct(channelId,productId);
    }

    // 채널 상품 복구
    public void includeProduct(UUID channelId, UUID productId){
        mapper.includeChannelProduct(channelId,productId);
    }

}
