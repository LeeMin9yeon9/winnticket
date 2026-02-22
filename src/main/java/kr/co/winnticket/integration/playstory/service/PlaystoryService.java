package kr.co.winnticket.integration.playstory.service;

import kr.co.winnticket.integration.playstory.client.PlaystoryClient;
import kr.co.winnticket.integration.playstory.config.PlaystoryConfig;
import kr.co.winnticket.integration.playstory.dto.*;
import kr.co.winnticket.integration.playstory.mapper.PlaystoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaystoryService {

    private final PlaystoryClient playstoryClient;
    private final PlaystoryMapper mapper;
    private final PlaystoryConfig config;

    public PlaystoryOrderResponse order(UUID orderId) {
        PlaystoryOrderRequest req = mapper.selectPlaystoryOrder(orderId);
        req.setChnId(config.getChnId());
        return playstoryClient.order(req);
    }

    public PlaystoryCheckResponse check(UUID orderId) {
        PlaystoryCheckRequest req = mapper.selectPlaystoryCheck(orderId);
        req.setChnId(config.getChnId());
        return playstoryClient.check(req);
    }

    public PlaystoryCheckCancelResponse cancel(UUID orderId) {
        PlaystoryCheckCancelRequest req = mapper.selectPlaystoryCancel(orderId);
        req.setChnId(config.getChnId());
        return playstoryClient.cancel(req);
    }
}
