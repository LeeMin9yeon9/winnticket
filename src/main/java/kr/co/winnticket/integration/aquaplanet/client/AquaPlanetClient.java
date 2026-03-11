package kr.co.winnticket.integration.aquaplanet.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AquaPlanetClient {

    private final RestTemplate aquaPlanetRestTemplate;

    private static final String URL =
            "https://exgatedev.hanwharesort.co.kr:443/iGate/SIF/json.jdo";

    public String call(String svcCd, String intfId, String dataKey, Map<String,Object> row){

        Map<String,Object> body = new HashMap<>();

        body.put("SystemHeader", createSystemHeader(svcCd,intfId));
        body.put("TransactionHeader", createTransactionHeader());
        body.put("MessageHeader", new HashMap<>());

        Map<String,Object> data = new HashMap<>();
        data.put(dataKey, List.of(row));

        body.put("Data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                aquaPlanetRestTemplate.exchange(
                        URL,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        return response.getBody();
    }

    private Map<String,Object> createSystemHeader(String svcCd,String intfId){

        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        Map<String,Object> header = new HashMap<>();

        header.put("TMSG_VER_DV_CD","01");
        header.put("ENVR_INFO_DV_CD","D");
        header.put("STN_MSG_ENCP_CD","0");
        header.put("STN_MSG_COMP_CD","0");
        header.put("LANG_CD","KO");
        header.put("TMSG_WRTG_DT",now.substring(0,8));
        header.put("TMSG_CRE_SYS_NM","SIF99999");
        header.put("STD_TMSG_SEQ_NO",System.currentTimeMillis()+"");
        header.put("STD_TMSG_PRGR_NO","00");
        header.put("STN_TMSG_IP","13.109.91.167");
        header.put("STN_TMSG_MAC","00-00-00-00-00-00");
        header.put("FRS_RQST_SYS_CD","SIF");
        header.put("FRS_RQST_DTM",now);
        header.put("TRMS_SYS_CD","SIF");
        header.put("RQST_RSPS_DV_CD","S");
        header.put("TRSC_SYNC_DV_CD","S");
        header.put("TMSG_RQST_DTM",now);
        header.put("RECV_SVC_CD",svcCd);
        header.put("INTF_ID",intfId);

        return header;
    }

    private Map<String,Object> createTransactionHeader(){

        Map<String,Object> header = new HashMap<>();

        header.put("STN_MSG_TR_TP_CD","O");
        header.put("SYSTEM_TYPE","HABIS");
        header.put("WRKR_NO","l1711019");
        header.put("MASK_AUTH","0");

        return header;
    }

}