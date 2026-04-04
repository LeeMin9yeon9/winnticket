package kr.co.winnticket.integration.aquaplanet.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCancelRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCancelResponse;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryItem;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryResponse;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetIssueRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetIssueResponse;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRecallRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRecallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AquaPlanetClient {

    private final RestTemplate aquaPlanetRestTemplate;
    private final ObjectMapper aquaPlanetObjectMapper;

    @Value("${aquaplanet.url}")
    private String url;

    @Value("${aquaplanet.system-name}")
    private String systemName;

    @Value("${aquaplanet.stn-tmsg-ip}")
    private String stnTmsgIp;

    @Value("${aquaplanet.wrkr-no}")
    private String wrkrNo;

    @Value("${aquaplanet.envr-dv-cd}")
    private String envrDvCd;

    public AquaPlanetIssueResponse issue(AquaPlanetIssueRequest req) {
        try {
            Map<String, Object> body = createBody(
                    "HBSSAMCPN0306",
                    "SIF00HBSSAMCPN0306",
                    "ds_input",
                    req
            );

            log.info("[AquaPlanet][ISSUE][REQ] ticketId={}, body={}", req.getTicketId(), body);

            String responseBody = send(body);

            log.info("[AquaPlanet][ISSUE][RES] ticketId={}, body={}", req.getTicketId(), responseBody);

            return parseIssueResponse(responseBody);

        } catch (Exception e) {
            log.error("[AquaPlanet][ISSUE][ERR] ticketId={}, message={}", req.getTicketId(), e.getMessage(), e);
            throw new RuntimeException("아쿠아플라넷 발행 실패", e);
        }
    }

    public AquaPlanetCancelResponse cancel(AquaPlanetCancelRequest req) {
        try {
            Map<String, Object> body = createBody(
                    "HBSSAMCPN1003",
                    "SIF00HBSSAMCPN1003",
                    "ds_input",
                    req
            );

            log.info("[AquaPlanet][CANCEL][REQ] ticketId={}, body={}", req.getTicketId(), body);

            String responseBody = send(body);

            log.info("[AquaPlanet][CANCEL][RES] ticketId={}, body={}", req.getTicketId(), responseBody);

            return parseCancelResponse(responseBody);

        } catch (Exception e) {
            log.error("[AquaPlanet][CANCEL][ERR] ticketId={}, message={}", req.getTicketId(), e.getMessage(), e);
            throw new RuntimeException("아쿠아플라넷 취소 실패", e);
        }
    }

    public AquaPlanetRecallResponse checkRecall(AquaPlanetRecallRequest req) {
        try {
            Map<String, Object> body = createBody(
                    "HBSSAMCPN1100",
                    "SIF00HBSSAMCPN1100",
                    "ds_input",
                    req
            );

            log.info("[AquaPlanet][RECALL][REQ] ticketId={}, body={}", req.getTicketId(), body);

            String responseBody = send(body);

            log.info("[AquaPlanet][RECALL][RES] ticketId={}, body={}", req.getTicketId(), responseBody);

            return parseRecallResponse(responseBody);

        } catch (Exception e) {
            log.error("[AquaPlanet][RECALL][ERR] ticketId={}, message={}", req.getTicketId(), e.getMessage(), e);
            throw new RuntimeException("아쿠아플라넷 회수조회 실패", e);
        }
    }

    private String send(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = aquaPlanetRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }

    private Map<String, Object> createBody(String svcCd, String intfId, String dataKey, Object row) {
        Map<String, Object> body = new HashMap<>();
        body.put("SystemHeader", createSystemHeader(svcCd, intfId));
        body.put("TransactionHeader", createTransactionHeader());
        body.put("MessageHeader", new HashMap<>());

        Map<String, Object> data = new HashMap<>();
        data.put(dataKey, List.of(row));
        body.put("Data", data);

        return body;
    }

    private Map<String, Object> createSystemHeader(String svcCd, String intfId) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        Map<String, Object> header = new HashMap<>();
        header.put("TMSG_VER_DV_CD", "01");
        header.put("ENVR_INFO_DV_CD", envrDvCd); // D(개발) / R(운영) 구분되어있음. aquaplanet.envr-dv-cd 같은 값으로 주입
        header.put("STN_MSG_ENCP_CD", "0");
        header.put("STN_MSG_COMP_CD", "0");
        header.put("LANG_CD", "KO");
        header.put("TMSG_WRTG_DT", now.substring(0, 8));
        header.put("TMSG_CRE_SYS_NM", systemName);
        header.put("STD_TMSG_SEQ_NO", (char)('A' + (int)(Math.random() * 26)) + String.valueOf(System.currentTimeMillis())); //Random(1) + unix time(13) = 14자리 | 앞에 랜덤 1 자리 추가
        header.put("STD_TMSG_PRGR_NO", "00");
        header.put("STN_TMSG_IP", stnTmsgIp);
        header.put("STN_TMSG_MAC", "00-00-00-00-00-00");
        header.put("FRS_RQST_SYS_CD", "SIF");
        header.put("FRS_RQST_DTM", now);
        header.put("TRMS_SYS_CD", "SIF");
        header.put("RQST_RSPS_DV_CD", "S");
        header.put("TRSC_SYNC_DV_CD", "S");
        header.put("TMSG_RQST_DTM", now);
        header.put("RECV_SVC_CD", svcCd);
        header.put("INTF_ID", intfId);
        return header;
    }

    private Map<String, Object> createTransactionHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("STN_MSG_TR_TP_CD", "O");
        header.put("SYSTEM_TYPE", "HABIS");
        header.put("WRKR_NO", wrkrNo);
        header.put("MASK_AUTH", "0");
        return header;
    }

    private AquaPlanetIssueResponse parseIssueResponse(String json) throws Exception {
        JsonNode root = aquaPlanetObjectMapper.readTree(json);

        String msgPrcsRsltCd = root.path("MessageHeader").path("MSG_PRCS_RSLT_CD").asText();
        if (!"0".equals(msgPrcsRsltCd)) {
            throw new RuntimeException(extractMessage(root));
        }

        JsonNode outputNode = root.path("Data").path("ds_output").get(0);
        if (outputNode == null || outputNode.isMissingNode()) {
            throw new RuntimeException("발행 응답 ds_output 없음");
        }

        return aquaPlanetObjectMapper.treeToValue(outputNode, AquaPlanetIssueResponse.class);
    }

    private AquaPlanetCancelResponse parseCancelResponse(String json) throws Exception {
        JsonNode root = aquaPlanetObjectMapper.readTree(json);

        String msgPrcsRsltCd = root.path("MessageHeader").path("MSG_PRCS_RSLT_CD").asText();
        if (!"0".equals(msgPrcsRsltCd)) {
            throw new RuntimeException(extractMessage(root));
        }

        JsonNode outputNode = root.path("Data").path("ds_output").get(0);
        if (outputNode == null || outputNode.isMissingNode()) {
            throw new RuntimeException("취소 응답 ds_output 없음");
        }

        return aquaPlanetObjectMapper.treeToValue(outputNode, AquaPlanetCancelResponse.class);
    }

    private AquaPlanetRecallResponse parseRecallResponse(String json) throws Exception {
        JsonNode root = aquaPlanetObjectMapper.readTree(json);

        String msgPrcsRsltCd = root.path("MessageHeader").path("MSG_PRCS_RSLT_CD").asText();

        if ("-1".equals(msgPrcsRsltCd)) {
            AquaPlanetRecallResponse empty = new AquaPlanetRecallResponse();
            empty.setDsResult(List.of());
            return empty;
        }

        if (!"0".equals(msgPrcsRsltCd)) {
            throw new RuntimeException(extractMessage(root));
        }

        JsonNode resultNode = root.path("Data").path("ds_result");
        AquaPlanetRecallResponse response = new AquaPlanetRecallResponse();
        if (resultNode == null || resultNode.isMissingNode()) {
            response.setDsResult(List.of());
            return response;
        }

        List<AquaPlanetRecallResponse.Result> results =
                aquaPlanetObjectMapper.readerForListOf(AquaPlanetRecallResponse.Result.class)
                        .readValue(resultNode);

        response.setDsResult(results);
        return response;
    }

    public AquaPlanetCouponHistoryResponse collectCouponHistory(AquaPlanetCouponHistoryRequest req) {
        try {
            Map<String, Object> body = createBody(
                    "HBSSAMCPN1103",
                    "SIF00HBSSAMCPN1103",
                    "ds_input",
                    req
            );

            log.info("[AquaPlanet][HISTORY][REQ] corpCd={}, contNo={}, bsnDate={}", req.getCorpCd(), req.getContNo(), req.getBsnDate());

            String responseBody = send(body);

            log.info("[AquaPlanet][HISTORY][RES] corpCd={}, contNo={}, body={}", req.getCorpCd(), req.getContNo(), responseBody);

            return parseCouponHistoryResponse(responseBody);

        } catch (Exception e) {
            log.error("[AquaPlanet][HISTORY][ERR] corpCd={}, contNo={}, bsnDate={}, message={}", req.getCorpCd(), req.getContNo(), req.getBsnDate(), e.getMessage(), e);
            throw new RuntimeException("아쿠아플라넷 쿠폰회수이력 조회 실패", e);
        }
    }

    private AquaPlanetCouponHistoryResponse parseCouponHistoryResponse(String json) throws Exception {
        JsonNode root = aquaPlanetObjectMapper.readTree(json);

        String msgPrcsRsltCd = root.path("MessageHeader").path("MSG_PRCS_RSLT_CD").asText();

        if ("-1".equals(msgPrcsRsltCd)) {
            AquaPlanetCouponHistoryResponse empty = new AquaPlanetCouponHistoryResponse();
            empty.setDsResult(List.of());
            return empty;
        }

        if (!"0".equals(msgPrcsRsltCd)) {
            throw new RuntimeException(extractMessage(root));
        }

        JsonNode resultNode = root.path("Data").path("ds_result");
        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        if (resultNode == null || resultNode.isMissingNode()) {
            response.setDsResult(List.of());
            return response;
        }

        List<AquaPlanetCouponHistoryItem> items =
                aquaPlanetObjectMapper.readerForListOf(AquaPlanetCouponHistoryItem.class)
                        .readValue(resultNode);

        response.setDsResult(items);
        return response;
    }

    private String extractMessage(JsonNode root) {
        JsonNode msgNode = root.path("MessageHeader").path("MSG_DATA_SUB");
        if (msgNode.isArray() && !msgNode.isEmpty()) {
            return msgNode.get(0).path("MSG_CTNS").asText("아쿠아플라넷 오류");
        }
        return "아쿠아플라넷 오류";
    }
}