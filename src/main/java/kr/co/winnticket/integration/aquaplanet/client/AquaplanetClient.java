package kr.co.winnticket.integration.aquaplanet.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.aquaplanet.dto.common.*;
import kr.co.winnticket.integration.aquaplanet.props.AquaplanetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Log4j2
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

        String reqJson;
        try {
            reqJson = aquaplanetObjectMapper.writeValueAsString(reqEnv);
        } catch (Exception e) {
            throw new RuntimeException("AquaPlanet 요청 JSON 생성 실패", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

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
        log.info("[AquaPlanet] url={} status={} raw={}", props.getBaseUrl(), resp.getStatusCode(), raw);

        try {
            // 응답은 Envelope 형태로 온다고 문서 예시가 있으니 Envelope로 파싱
            // Data만 타입 맞추기 위해 중간에 tree로 처리
            var root = aquaplanetObjectMapper.readTree(raw);

            var systemHeaderNode = root.get("SystemHeader");
            var trxHeaderNode = root.get("TransactionHeader");
            var msgHeaderNode = root.get("MessageHeader");
            var dataNode = root.get("Data");

            AquaplanetSystemHeader sh = aquaplanetObjectMapper.treeToValue(systemHeaderNode, AquaplanetSystemHeader.class);
            AquaplanetTransactionHeader th = aquaplanetObjectMapper.treeToValue(trxHeaderNode, AquaplanetTransactionHeader.class);
            AquaplanetMessageHeader mh = (msgHeaderNode == null || msgHeaderNode.isNull())
                    ? AquaplanetMessageHeader.builder().build()
                    : aquaplanetObjectMapper.treeToValue(msgHeaderNode, AquaplanetMessageHeader.class);

            TRes data = (dataNode == null || dataNode.isNull())
                    ? null
                    : aquaplanetObjectMapper.treeToValue(dataNode, dataResClass);

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
