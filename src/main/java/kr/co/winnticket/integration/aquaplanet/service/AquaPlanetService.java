package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AquaPlanetService {

    private final AquaPlanetClient client;

    public String searchProducts(AquaPlanetProductRequest req){

        Map<String,Object> row = new HashMap<>();
        row.put("CORP_CD",req.getCorpCd());
        row.put("CONT_NO",req.getContNo());
        row.put("STDR_DATE",req.getStdrDate());

        return client.call(
                "HBSSAMCNT0114",
                "SIF00HBSSAMCNT0114",
                "ds_search",
                row
        );
    }

    public String issueCoupon(AquaPlanetIssueRequest req){

        Map<String,Object> row = new HashMap<>();

        row.put("CORP_CD",req.getCorpCd());
        row.put("CONT_NO",req.getContNo());
        row.put("SEQ",req.getSeq());
        row.put("ISSUE_DATE",req.getIssueDate());
        row.put("GOODS_NO",req.getGoodsNo());
        row.put("ISSUE_QTY",req.getIssueQty());
        row.put("UNITY_ISSUE_YN",req.getUnityIssueYn());
        row.put("RCVER_NM",req.getRcverNm());
        row.put("RCVER_TEL_NATION_NO",req.getRcverTelNationNo());
        row.put("RCVER_TEL_AREA_NO",req.getRcverTelAreaNo());
        row.put("RCVER_TEL_EXCHGE_NO",req.getRcverTelExchgeNo());
        row.put("RCVER_TEL_NO",req.getRcverTelNo());

        return client.call(
                "HBSSAMCPN0306",
                "SIF00HBSSAMCPN0306",
                "ds_input",
                row
        );
    }

    public String cancelCoupon(AquaPlanetCancelRequest req){

        Map<String,Object> row = new HashMap<>();

        row.put("CORP_CD",req.getCorpCd());
        row.put("CONT_NO",req.getContNo());
        row.put("REPR_CPON_INDICT_NO",req.getReprCponIndictNo());

        return client.call(
                "HBSSAMCPN1003",
                "SIF00HBSSAMCPN1003",
                "ds_input",
                row
        );
    }

}