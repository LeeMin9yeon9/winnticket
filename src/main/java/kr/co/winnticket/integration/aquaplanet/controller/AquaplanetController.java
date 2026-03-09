package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetCommonRequest;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductRequest;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductResponse;
import kr.co.winnticket.integration.aquaplanet.dto.coupon.*;
import kr.co.winnticket.integration.aquaplanet.service.AquaplanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquaplanet/test")
public class AquaplanetController {
    private final String HABIS_URL = "https://exgatedev.hanwharesort.co.kr/iGate/SIF/json.jdo";

    @Operation(summary = "쿠폰 발행 (SIF00HBSSAMCPN0306)")
    @PostMapping("/coupon/issue")
    public Object issue(@RequestBody Map<String, Object> params) {
        // params 필수: corpCd, contNo, goodsNo, rcverNm, telNo (01012345678)
        Map<String, Object> ds = new HashMap<>();
        ds.put("CORP_CD", params.get("corpCd"));
        ds.put("CONT_NO", params.get("contNo"));
        ds.put("SEQ", System.currentTimeMillis() % 100000); // 대행사 채번 순번
        ds.put("ISSUE_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        ds.put("GOODS_NO", params.get("goodsNo"));
        ds.put("ISSUE_QTY", 1);
        ds.put("UNITY_ISSUE_YN", "N");
        ds.put("RCVER_NM", params.get("rcverNm"));

        // 전화번호 파싱 (명세서 33P 규격)
        String tel = (String) params.get("telNo");
        ds.put("RCVER_TEL_NATION_NO", "82");
        ds.put("RCVER_TEL_AREA_NO", tel.substring(0, 3));
        ds.put("RCVER_TEL_EXCHGE_NO", tel.substring(3, tel.length() - 4));
        ds.put("RCVER_TEL_NO", tel.substring(tel.length() - 4));

        return send("HBSSAMCPN0306", "SIF00HBSSAMCPN0306", "ds_input", ds);
    }

    @Operation(summary = "발행 취소 (SIF00HBSSAMCPN1003)")
    @PostMapping("/coupon/cancel")
    public Object cancel(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String indictNo) {
        Map<String, Object> ds = new HashMap<>();
        ds.put("CORP_CD", corpCd);
        ds.put("CONT_NO", contNo);
        ds.put("REPR_CPON_INDICT_NO", indictNo);

        return send("HBSSAMCPN1003", "SIF00HBSSAMCPN1003", "ds_input", ds);
    }

    @Operation(summary = "회수 실적 조회 (SIF00HBSSAMCPN1103)")
    @GetMapping("/coupon/history")
    public Object getHistory(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String bsnDate) {
        Map<String, Object> ds = new HashMap<>();
        ds.put("CORP_CD", corpCd);
        ds.put("CONT_NO", contNo);
        ds.put("BSN_DATE", bsnDate); // YYYYMMDD

        return send("HBSSAMCPN1103", "SIF00HBSSAMCPN1103", "ds_input", ds);
    }

    private Object send(String svcCd, String intfId, String dataKey, Map<String, Object> dataRow) {
        String ymd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ymdhms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        AquaplanetCommonRequest req = AquaplanetCommonRequest.builder()
                .SystemHeader(AquaplanetCommonRequest.SystemHeader.builder()
                        .ENVR_INFO_DV_CD("D")
                        .TMSG_WRTG_DT(ymd)
                        .TMSG_CRE_SYS_NM("SIF" + String.format("%05d", new Random().nextInt(10000)))
                        .STD_TMSG_SEQ_NO(new Random().nextInt(10) + String.valueOf(System.currentTimeMillis()))
                        .STN_TMSG_IP("13.109.91.167")
                        .FRS_RQST_DTM(ymdhms)
                        .TMSG_RQST_DTM(ymdhms)
                        .RECV_SVC_CD(svcCd)
                        .INTF_ID(intfId)
                        .build())
                .TransactionHeader(AquaplanetCommonRequest.TransactionHeader.builder()
                        .CORP_CD((String)dataRow.get("CORP_CD"))
                        .CMP_NO((String)dataRow.get("CORP_CD"))
                        .build())
                .MessageHeader(new HashMap<>())
                .Data(Collections.singletonMap(dataKey, Collections.singletonList(dataRow)))
                .build();

        return new RestTemplate().postForObject(HABIS_URL, req, Object.class);
    }
}
