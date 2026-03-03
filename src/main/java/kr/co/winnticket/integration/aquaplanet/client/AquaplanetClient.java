package kr.co.winnticket.integration.aquaplanet.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.aquaplanet.dto.common.*;
import kr.co.winnticket.integration.aquaplanet.props.AquaplanetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AquaplanetClient {

    private final RestTemplate aquaplanetRestTemplate;
    private final ObjectMapper aquaplanetObjectMapper;
    private final AquaplanetProperties props;

    public <TReq, TRes> AquaplanetEnvelope<TRes> call(
            String recvSvcCd,
            String intfId,
            TReq dataReq,
            Class<TRes> dataResClass
    ) {
        AquaplanetEnvelope<TReq> reqEnv = AquaplanetEnvelope.<TReq>builder()
                .systemHeader(buildSystemHeader(recvSvcCd, intfId))
                .transactionHeader(buildTransactionHeader())
                .messageHeader(AquaplanetMessageHeader.builder().build())
                .data(dataReq)
                .build();

        final String reqJson;
        try {
            reqJson = aquaplanetObjectMapper.writeValueAsString(reqEnv);
        } catch (Exception e) {
            throw new RuntimeException("AquaPlanet 요청 JSON 생성 실패", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(reqJson, headers);

        ResponseEntity<String> resp;
        try {
            resp = aquaplanetRestTemplate.exchange(
                    props.getBaseUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        } catch (Exception e) {
            throw new RuntimeException("AquaPlanet HTTP 호출 실패", e);
        }

        String raw = resp.getBody();
        log.info("[AquaPlanet] url={} status={} req={} raw={}", props.getBaseUrl(), resp.getStatusCode(), reqJson, raw);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("AquaPlanet HTTP 비정상: " + resp.getStatusCode());
        }

        try {
            JsonNode root = aquaplanetObjectMapper.readTree(raw);

            AquaplanetSystemHeader sh = nodeTo(root, "SystemHeader", AquaplanetSystemHeader.class);
            AquaplanetTransactionHeader th = nodeTo(root, "TransactionHeader", AquaplanetTransactionHeader.class);
            AquaplanetMessageHeader mh = nodeTo(root, "MessageHeader", AquaplanetMessageHeader.class);

            // ✅ 문서 기준 정상 = MSG_PRCS_RSLT_CD == "0"
            if (mh != null && mh.getMsgPrcsRsltCd() != null && !"0".equals(mh.getMsgPrcsRsltCd())) {
                String msg = extractMsg(mh);
                throw new RuntimeException("AquaPlanet 실패: " + mh.getMsgPrcsRsltCd() + " / " + msg);
            }

            TRes data = null;
            JsonNode dataNode = root.get("Data");
            if (dataNode != null && !dataNode.isNull()) {
                data = aquaplanetObjectMapper.treeToValue(dataNode, dataResClass);
            }

            return AquaplanetEnvelope.<TRes>builder()
                    .systemHeader(sh)
                    .transactionHeader(th)
                    .messageHeader(mh)
                    .data(data)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("AquaPlanet 응답 파싱 실패", e);
        }
    }

    private String extractMsg(AquaplanetMessageHeader mh) {
        if (mh.getMsgDataSub() == null || mh.getMsgDataSub().isEmpty()) return "";
        AquaplanetMessageHeader.MessageItem first = mh.getMsgDataSub().get(0);
        return first == null ? "" : (first.getMsgCd() + " / " + first.getMsgCtns());
    }

    private <T> T nodeTo(JsonNode root, String key, Class<T> clazz) throws Exception {
        JsonNode n = root.get(key);
        if (n == null || n.isNull()) return null;
        return aquaplanetObjectMapper.treeToValue(n, clazz);
    }

    private AquaplanetSystemHeader buildSystemHeader(String recvSvcCd, String intfId) {
        String today = AquaplanetUtil.yyyymmddNow();
        String now17 = AquaplanetUtil.dt17Now();

        return AquaplanetSystemHeader.builder()
                .stdTmsgLen(null)
                .tmsgVerDvCd(props.getTmsgVerDvCd())
                .envrInfoDvCd(props.getEnvrInfoDvCd())
                .stnMsgEncpCd(props.getStnMsgEncpCd())
                .stnMsgCompCd(props.getStnMsgCompCd())
                .langCd(props.getLangCd())
                .tmsgWrtgDt(today)
                .tmsgCreSysNm("SIF" + AquaplanetUtil.randomDigits(5))
                .stdTmsgSeqNo(AquaplanetUtil.stdSeqNo())
                .stdTmsgPrgrNo(props.getStdTmsgPrgrNo())
                .stnTmsgIp(AquaplanetUtil.localIpOrEmpty())
                .stnTmsgMac("")
                .frsRqstSysCd(props.getFrsRqstSysCd())
                .frsRqstDtm(now17)
                .trmsSysCd("SIF")
                .trmsNdNo("")
                .rqstRspsDvCd(props.getRqstRspsDvCd())
                .trscSyncDvCd(props.getTrscSyncDvCd())
                .tmsgRqstDtm(now17)
                .recvSvcCd(recvSvcCd)
                .intfId(intfId)
                .tmsgRspsDtm("")
                .prcsRsltCd("")
                .errOccSysCd("")
                .stnTmsgErrCd("")
                .mciNodeNo("")
                .remtIp("")
                .mciSsnId("")
                .filler("")
                .build();
    }

    private AquaplanetTransactionHeader buildTransactionHeader() {
        return AquaplanetTransactionHeader.builder()
                .stnMsgTrTpCd(props.getStnMsgTrTpCd())
                .systemType(props.getSystemType())
                .screenShortenNo("")
                .screenId("")
                .corpCd("")
                .cmpNo("")
                .branchNo("")
                .locCd("")
                .wrkrNo(props.getWrkrNo())
                .persInfoMask("")
                .maskAuth(props.getMaskAuth())
                .osdeTrCd("")
                .osdeTrOrgCd("")
                .osdeTrMsgCd("")
                .osdeTrJobCd("")
                .osdeTrRutnId("")
                .osdeTrPrgNo("")
                .filler("")
                .build();
    }
}
