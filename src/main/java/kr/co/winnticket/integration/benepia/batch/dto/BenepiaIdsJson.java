package kr.co.winnticket.integration.benepia.batch.dto;

import lombok.Data;

import java.util.List;

@Data
public class BenepiaIdsJson {
    private List<IdItem> productIds;

    @Data
    public static class IdItem {
        public String coopCoCd;
        public String prdId;
    }
}
