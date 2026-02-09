package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankdaTransaction {

    private String bkcode;
    private String accountnum;
    private String bkname;
    private String bkdate;
    private String bktime;
    private String bkjukyo;
    private String bkcontent;
    private String bketc;

    private String bkinput;
    private String bkoutput;
    private String bkjango;
}