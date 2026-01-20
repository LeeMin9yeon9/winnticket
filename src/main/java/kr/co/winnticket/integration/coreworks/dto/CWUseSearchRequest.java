package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;

@Data
public class CWUseSearchRequest {
    private String channelCd;
    private String useStartDate;   // yyyyMMdd
    private String useEndDate;     // yyyyMMdd
}
