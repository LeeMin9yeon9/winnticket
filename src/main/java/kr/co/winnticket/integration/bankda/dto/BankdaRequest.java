package kr.co.winnticket.integration.bankda.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankdaRequest {

    private String accountnum;
    private String datefrom;
    private String dateto;
    private String bkcode;
    private String istest;
}
