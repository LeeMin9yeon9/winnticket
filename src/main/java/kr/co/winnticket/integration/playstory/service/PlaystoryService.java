package kr.co.winnticket.integration.playstory.service;

import kr.co.winnticket.integration.playstory.client.PlaystoryClient;
import kr.co.winnticket.integration.playstory.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaystoryService {

    private final PlaystoryClient playstoryClient;

    public PlaystoryOrderResponse order(PlaystoryOrderRequest req) {
        return playstoryClient.order(req);
    }

    public PlaystoryCheckResponse check(PlaystoryCheckRequest req) {
        return playstoryClient.check(req);
    }

    public PlaystoryCheckCancelResponse cancel(PlaystoryCheckCancelRequest req) {
        return playstoryClient.cancel(req);
    }
}
