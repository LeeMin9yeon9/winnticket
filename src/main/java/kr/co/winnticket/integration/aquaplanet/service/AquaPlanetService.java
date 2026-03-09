package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaPlanetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AquaPlanetService {
    private final AquaPlanetClient client;
    private final AquaPlanetMapper mapper;

    // 1. 상품조회
    public Map searchProducts(String corpCd, String contNo) {
        return client.execute("HBSSAMCNT0114", "SIF00HBSSAMCNT0114",
                Map.of("ds_search", List.of(Map.of("CORP_CD", corpCd, "CONT_NO", contNo))));
    }

    // 2. 쿠폰발행
    @Transactional
    public void issue(UUID orderId) {
        var input = mapper.selectIssueData(orderId);
        var res = client.execute("HBSSAMCPN0306", "SIF00HBSSAMCPN0306", Map.of("ds_input", List.of(input)));

        // 결과 파싱 및 저장 (문서의 ds_output 구조 참조)
        List<Map> output = (List<Map>) ((Map)res.get("Data")).get("ds_output");
        String couponNo = String.valueOf(output.get(0).get("REPR_CPON_INDICT_NO"));
        mapper.updateTicketCoupon(orderId, couponNo);
    }

    // 3. 발행취소
    public Map cancel(String corpCd, String contNo, String couponNo) {
        return client.execute("HBSSAMCPN1003", "SIF00HBSSAMCPN1003",
                Map.of("ds_input", List.of(Map.of("CORP_CD", corpCd, "CONT_NO", contNo, "REPR_CPON_INDICT_NO", couponNo))));
    }

    // 4. 개별 회수조회
    public Map getHistory(String couponNo) {
        return client.execute("HBSSAMCPN1100", "SIF00HBSSAMCPN1100",
                Map.of("ds_input", List.of(Map.of("REPR_CPON_INDICT_NO", couponNo))));
    }

    // 5. 영업일별 회수조회 (정산용)
    public Map getDailyHistory(String bsnDate) {
        return client.execute("HBSSAMCPN1103", "SIF00HBSSAMCPN1103",
                Map.of("ds_input", List.of(Map.of("BSN_DATE", bsnDate))));
    }
}