package kr.co.winnticket.integration.aquaplanet.dto;

import lombok.Data;

@Data
public class AquaPlanetRecallRequest {

    private String corpCd;
    private String contNo;
    private String issueDate;
    private String reprCponSeq;
    private String reprCponIndictNo;

}