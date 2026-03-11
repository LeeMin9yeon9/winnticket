package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AquaPlanetRequest {

    @JsonProperty("SystemHeader")
    private Object systemHeader;

    @JsonProperty("TransactionHeader")
    private Object transactionHeader;

    @JsonProperty("MessageHeader")
    private Object messageHeader;

    @JsonProperty("Data")
    private Object data;

}