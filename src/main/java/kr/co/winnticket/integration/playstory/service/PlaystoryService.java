package kr.co.winnticket.integration.playstory.service;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.playstory.client.PlaystoryClient;
import kr.co.winnticket.integration.playstory.config.PlaystoryConfig;
import kr.co.winnticket.integration.playstory.dto.*;
import kr.co.winnticket.integration.playstory.mapper.PlaystoryMapper;
import kr.co.winnticket.integration.playstory.mapper.PlaystoryResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlaystoryService {

    private final PlaystoryClient playstoryClient;
    private final PlaystoryMapper mapper;
    private final PlaystoryConfig config;
    private final PlaystoryResponseMapper responseMapper;

    public PlaystoryOrderResponse order(UUID orderId) {
        PlaystoryOrderRequest req = mapper.selectPlaystoryOrder(orderId);
        req.setChnId(config.getChnId());

        PlaystoryOrderResponse response = playstoryClient.order(req);

        IntegrationResult result = responseMapper.mapOrder(response);

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "플레이스토리 주문 실패 - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }

        return response;
    }

    public PlaystoryCheckResponse check(UUID orderId) {

        PlaystoryCheckRequest req = mapper.selectPlaystoryCheck(orderId);
        req.setChnId(config.getChnId());

        PlaystoryCheckResponse response = playstoryClient.check(req);

        IntegrationResult result = responseMapper.mapCheck(response);

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "플레이스토리 조회 실패 - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }

        return response;
    }

    public PlaystoryCheckCancelResponse cancel(UUID orderId) {

        PlaystoryCheckCancelRequest req = mapper.selectPlaystoryCancel(orderId);
        req.setChnId(config.getChnId());

        PlaystoryCheckCancelResponse response = playstoryClient.cancel(req);

        IntegrationResult result = responseMapper.mapCancel(response);

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "플레이스토리 취소 실패 - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }

        return response;
    }
}
