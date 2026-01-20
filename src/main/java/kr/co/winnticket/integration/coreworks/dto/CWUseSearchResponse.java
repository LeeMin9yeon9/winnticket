package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;
import java.util.List;

@Data
public class CWUseSearchResponse {

    private List<Pin> pinList;

    @Data
    public static class Pin {
        private String orderSeq;
        private String pin;
        private String code;
        private String useDate;
    }
}
