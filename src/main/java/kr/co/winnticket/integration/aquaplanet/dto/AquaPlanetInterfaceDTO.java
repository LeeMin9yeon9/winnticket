package kr.co.winnticket.integration.aquaplanet.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

public class AquaPlanetInterfaceDTO {
    // 1. 상품조회 (HBSSAMCNT0114)
    @Data public static class ProductReq { private List<Map<String, String>> ds_search; }

    // 2. 쿠폰발행 (HBSSAMCPN0306)
    @Data public static class IssueReq { private List<IssueInput> ds_input; }
    @Data public static class IssueInput {
        private String CORP_CD; private String CONT_NO; private String SEQ;
        private String ISSUE_DATE; private String GOODS_NO; private Integer ISSUE_QTY;
        private String RCVER_NM; private String RCVER_TEL_AREA_NO;
        private String RCVER_TEL_EXCHGE_NO; private String RCVER_TEL_NO;
    }

    // 3. 발행취소 (HBSSAMCPN1003)
    @Data public static class CancelReq { private List<Map<String, String>> ds_input; }

    // 4. 회수이력 (HBSSAMCPN1100)
    @Data public static class HistoryReq { private List<Map<String, String>> ds_input; }

    // 5. 영업일별 회수 (HBSSAMCPN1103)
    @Data public static class DailyHistoryReq { private List<Map<String, String>> ds_input; }
}