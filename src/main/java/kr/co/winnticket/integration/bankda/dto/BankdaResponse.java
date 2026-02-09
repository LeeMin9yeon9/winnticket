package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankdaResponse {

    private Request request;
    private Response response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {

        private String accountnum;
        private String bkname;
        private String bkcode;
        private String datefrom;
        private String dateto;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {

        private int record;
        private String description;
        private List<BankdaTransaction> bank;
    }
}
