package kr.co.winnticket.integration.smartinfini.service;

import kr.co.winnticket.integration.smartinfini.client.SmartInfiniClient;
import kr.co.winnticket.integration.smartinfini.dto.SICancelRequest;
import kr.co.winnticket.integration.smartinfini.dto.SICancelResponse;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderRequest;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmartInfiniService {

    private final SmartInfiniClient client;

    public SIOrderResponse order(SIOrderRequest req) {
        SIOrderResponse res = client.order(req);
        return res;
    }

    public SICancelResponse cancel(SICancelRequest req) {
        SICancelResponse res = client.cancel(req);
        return res;
    }
}