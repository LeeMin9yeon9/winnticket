package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "AquaPlanet API", description = "아쿠아플라넷(한화) 연동 인터페이스")
@RestController
@RequestMapping("/api/aquaplanet/test")
@RequiredArgsConstructor
public class AquaPlanetController {

    private final AquaPlanetService aquaPlanetService;

    @PostMapping("/issue")
    @Operation(summary = "쿠폰 발행 (HBSSAMCPN0306)")
    public String issue(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String goodsNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("GOODS_NO", goodsNo);
        body.put("SEQ", 1);
        body.put("ISSUE_DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        body.put("ISSUE_QTY", 1);
        body.put("UNITY_ISSUE_YN", "N");
        body.put("RCVER_NM", "테스터");
        body.put("RCVER_TEL_NATION_NO", "82");
        body.put("RCVER_TEL_AREA_NO", "010");
        body.put("RCVER_TEL_EXCHGE_NO", "1234");
        body.put("RCVER_TEL_NO", "5678");

        return aquaPlanetService.aquaPlanetRequest("HBSSAMCPN0306", corpCd, body, "ds_input");
    }

    @PostMapping("/cancel")
    @Operation(summary = "쿠폰 취소 (HBSSAMCPN1003)")
    public String cancel(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String couponNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("REPR_CPON_INDICT_NO", couponNo);

        return aquaPlanetService.aquaPlanetRequest("HBSSAMCPN1003", corpCd, body, "ds_input");
    }

    @GetMapping("/search-goods")
    @Operation(summary = "상품 조회 (HBSSAMCNT0114)")
    public String searchGoods(@RequestParam String corpCd, @RequestParam String contNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("STDR_DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return aquaPlanetService.aquaPlanetRequest("HBSSAMCNT0114", corpCd, body, "ds_search");
    }

    @GetMapping("/history-single")
    @Operation(summary = "개별 회수 조회 (HBSSAMCPN1100)")
    public String historySingle(@RequestParam String corpCd, @RequestParam String contNo, @RequestParam String couponNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("ISSUE_DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        body.put("REPR_CPON_SEQ", "1");
        body.put("REPR_CPON_INDICT_NO", couponNo);

        return aquaPlanetService.aquaPlanetRequest("HBSSAMCPN1100", corpCd, body, "ds_input");
    }

    @GetMapping("/history-daily")
    @Operation(summary = "일자별 회수 조회 (HBSSAMCPN1103)")
    public String historyDaily(@RequestParam String corpCd, @RequestParam String contNo) {
        Map<String, Object> body = new HashMap<>();
        body.put("CORP_CD", corpCd);
        body.put("CONT_NO", contNo);
        body.put("BSN_DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return aquaPlanetService.aquaPlanetRequest("HBSSAMCPN1103", corpCd, body, "ds_input");
    }
}