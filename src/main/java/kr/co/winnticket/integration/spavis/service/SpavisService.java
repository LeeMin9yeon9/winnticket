package kr.co.winnticket.integration.spavis.service;
import io.swagger.v3.oas.models.PathItem;
import kr.co.winnticket.integration.spavis.client.SpavisClient;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import kr.co.winnticket.integration.spavis.mapper.SpavisMapper;
import kr.co.winnticket.integration.spavis.mapper.SpavisResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpavisService {

    private final SpavisClient client;
    private final SpavisMapper mapper;
    private final SpavisResponseMapper responseMapper;

    public List<SPCouponCheckResponse> check(UUID orderId) throws Exception {
        // 1. 모든 쿠폰 번호를 리스트로 가져옴
        List<String> couponNos = mapper.selectCouponNo(orderId);

        if (couponNos == null || couponNos.isEmpty()) {
            throw new RuntimeException("조회된 쿠폰 번호가 없습니다. orderId: " + orderId);
        }

        List<SPCouponCheckResponse> results = new java.util.ArrayList<>();

        // 2. 루프를 돌며 각 쿠폰의 상태를 개별 조회
        for (String couponNo : couponNos) {
            try {
                SPCouponCheckResponse res = client.checkCoupon(couponNo);

                // 응답 매핑 및 검증
                var result = responseMapper.mapCheck(res);
                if (result.isSuccess()) {
                    results.add(res);
                } else {
                    log.warn("[Spavis] 쿠폰 일부 조회 실패: couponNo={}, msg={}", couponNo, result.getMessage());
                }
            } catch (Exception e) {
                log.error("[Spavis] 쿠폰 조회 중 에러 발생: couponNo={}", couponNo, e);
                // 한 건이 에러 나더라도 나머지는 계속 진행하기 위해 throw 대신 log만 남김
            }
        }

        // 3. 전체 결과 리스트 반환
        return results;
    }
}
