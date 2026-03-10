package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetInterfaceDTO.IssueInput;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaPlanetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AquaPlanetService {

    private final AquaPlanetClient client;
    private final AquaPlanetMapper mapper;

    public Map getProducts(String corpCd, String contNo) {
        Map<String, Object> data = Map.of("ds_search", List.of(Map.of("CORP_CD", corpCd, "CONT_NO", contNo)));
        return client.execute("HBSSAMCNT0114", "SIF00HBSSAMCNT0114", data);
    }

    @Transactional
    public void issueCoupon(UUID orderId) {
        // 1. DB에서 발행에 필요한 데이터(상품코드, 수량, 수신자정보 등) 조회
        IssueInput input = mapper.selectAquaPlanetIssueData(orderId);
        if (input == null) {
            throw new RuntimeException("발행 대상 주문 데이터를 찾을 수 없습니다. orderId: " + orderId);
        }

        // 2. 한화 API 호출 (POST)
        Map<String, Object> response = client.execute("HBSSAMCPN0306", "SIF00HBSSAMCPN0306",
                Map.of("ds_input", List.of(input)));

        // 3. 응답 결과 분석 (성공 여부 판단)
        // 한화 규격: MessageHeader.MSG_PRCS_RSLT_CD가 '0'이면 비즈니스 로직 성공
        Map<String, Object> messageHeader = (Map<String, Object>) response.get("MessageHeader");
        String resultCode = String.valueOf(messageHeader.get("MSG_PRCS_RSLT_CD"));

        if ("0".equals(resultCode)) {
            // 4. 성공 시 쿠폰번호 추출
            Map<String, Object> dataPart = (Map<String, Object>) response.get("Data");
            List<Map<String, Object>> dsOutput = (List<Map<String, Object>>) dataPart.get("ds_output");

            if (dsOutput != null && !dsOutput.isEmpty()) {
                // 정의서상 발행된 쿠폰번호 필드명: CPON_INDICT_NO (또는 REPR_CPON_INDICT_NO 확인 필요)
                String partnerCouponNo = String.valueOf(dsOutput.get(0).get("CPON_INDICT_NO"));

                // 5. 우리 DB 업데이트 (쿠폰번호 저장 및 상태 변경)
                mapper.updateTicketPartnerInfo(orderId, partnerCouponNo);
            } else {
                throw new RuntimeException("한화 응답 성공이나 쿠폰 번호가 데이터에 없습니다.");
            }
        } else {
            // 6. 실패 시 에러 메시지 추출 및 예외 처리
            List<Map<String, Object>> msgDataSub = (List<Map<String, Object>>) messageHeader.get("MSG_DATA_SUB");
            String errorMsg = "한화 API 오류";
            if (msgDataSub != null && !msgDataSub.isEmpty()) {
                errorMsg = String.valueOf(msgDataSub.get(0).get("MSG_CTNS"));
            }
            throw new RuntimeException("쿠폰 발행 실패: " + errorMsg);
        }
    }

    public Map cancelCoupon(String corpCd, String contNo, String couponNo) {
        Map<String, Object> data = Map.of("ds_input", List.of(Map.of(
                "CORP_CD", corpCd, "CONT_NO", contNo, "REPR_CPON_INDICT_NO", couponNo)));
        return client.execute("HBSSAMCPN1003", "SIF00HBSSAMCPN1003", data);
    }

    public Map getDailyHistory(String bsnDate) {
        Map<String, Object> data = Map.of("ds_input", List.of(Map.of(
                "CORP_CD", "4000", "CONT_NO", "11900078", "BSN_DATE", bsnDate)));
        return client.execute("HBSSAMCPN1103", "SIF00HBSSAMCPN1103", data);
    }
}