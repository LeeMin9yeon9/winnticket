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
    private final String DEV_URL = "https://exgatedev.hanwharesort.co.kr/iGate/SIF/json.jdo";

    @Operation(summary = "1. 계약사 상품조회 (HBSSAMCNT0114)")
    @PostMapping("/search-goods")
    public Object searchGoods(@RequestParam String corpCd, @RequestParam String contNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("STDR_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return callHabis("HBSSAMCNT0114", "SIF00HBSSAMCNT0114", "ds_search", body);
    }

    @Operation(summary = "2. 쿠폰 발행 처리 (HBSSAMCPN0306)")
    @PostMapping("/issue")
    public Object issueCoupon(
            @RequestParam String corpCd,
            @RequestParam String contNo,
            @RequestParam String goodsNo,
            @RequestParam String rcverNm,
            @RequestParam String telNo) { // telNo 예: 01012345678

        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("SEQ", System.currentTimeMillis() % 100000); // 대행사 관리 순번
        body.put("ISSUE_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        body.put("GOODS_NO", goodsNo);
        body.put("ISSUE_QTY", 1);
        body.put("UNITY_ISSUE_YN", "N");
        body.put("RCVER_NM", rcverNm);
        body.put("RCVER_TEL_NATION_NO", "82");
        body.put("RCVER_TEL_AREA_NO", telNo.substring(0, 3));
        body.put("RCVER_TEL_EXCHGE_NO", telNo.substring(3, telNo.length()-4));
        body.put("RCVER_TEL_NO", telNo.substring(telNo.length()-4));

        return callHabis("HBSSAMCPN0306", "SIF00HBSSAMCPN0306", "ds_input", body);
    }

    @Operation(summary = "3. 쿠폰 발행 취소 (HBSSAMCPN1003)")
    @PostMapping("/cancel")
    public Object cancelCoupon(
            @RequestParam String corpCd,
            @RequestParam String contNo,
            @RequestParam String reprCponIndictNo) {

        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("REPR_CPON_INDICT_NO", reprCponIndictNo);

        return callHabis("HBSSAMCPN1003", "SIF00HBSSAMCPN1003", "ds_input", body);
    }

    @Operation(summary = "4. 영업일자별 회수 이력 조회 (HBSSAMCPN1103)")
    @PostMapping("/history-daily")
    public Object historyDaily(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String bsnDate) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("BSN_DATE", bsnDate); // YYYYMMDD

        return callHabis("HBSSAMCPN1103", "SIF00HBSSAMCPN1103", "ds_input", body);
    }

    private Object callHabis(String svcCd, String intfId, String dataKey, Map<String, Object> inputBody) {
        String nowYmd = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nowFull = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomSeq = (int)(Math.random() * 10) + String.valueOf(System.currentTimeMillis());

        AquaplanetCommonRequest request = AquaplanetCommonRequest.builder()
                .SystemHeader(AquaplanetCommonRequest.SystemHeader.builder()
                        .TMSG_VER_DV_CD("01")
                        .ENVR_INFO_DV_CD("D")
                        .STN_MSG_ENCP_CD("0")
                        .STN_MSG_COMP_CD("0")
                        .LANG_CD("KO")
                        .TMSG_WRTG_DT(nowYmd)
                        .TMSG_CRE_SYS_NM("SIF" + String.format("%05d", new Random().nextInt(10000)))
                        .STD_TMSG_SEQ_NO(randomSeq)
                        .STD_TMSG_PRGR_NO("00")
                        .STN_TMSG_IP("13.109.91.167")
                        .FRS_RQST_SYS_CD("SIF")
                        .FRS_RQST_DTM(nowFull)
                        .TRMS_SYS_CD("SIF")
                        .RQST_RSPS_DV_CD("S")
                        .TRSC_SYNC_DV_CD("S")
                        .TMSG_RQST_DTM(nowFull)
                        .RECV_SVC_CD(svcCd)
                        .INTF_ID(intfId)
                        .build())
                .TransactionHeader(AquaplanetCommonRequest.TransactionHeader.builder()
                        .STN_MSG_TR_TP_CD("O")
                        .SYSTEM_TYPE("HABIS")
                        .CORP_CD(inputBody.get("CORP_CD").toString())
                        .CMP_NO(inputBody.get("CORP_CD").toString())
                        .WRKR_NO("l1711019")
                        .MASK_AUTH("0")
                        .build())
                .MessageHeader(new HashMap<>())
                .Data(Map.of(dataKey, Collections.singletonList(inputBody)))
                .build();

        return new RestTemplate().postForObject(DEV_URL, request, Object.class);
    }
}
