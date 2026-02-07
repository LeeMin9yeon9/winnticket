package kr.co.winnticket.integration.spavis.service;
import io.swagger.v3.oas.models.PathItem;
import kr.co.winnticket.integration.spavis.client.SpavisClient;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpHeaders;

@Service
@RequiredArgsConstructor
public class SpavisService {

    private final SpavisClient client;

    public SPCouponCheckResponse check(String couponNo) throws Exception {
        return client.checkCoupon(couponNo);
    }
}
