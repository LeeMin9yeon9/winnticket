package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AquaplanetEnvelope<T> {

    @JsonProperty("SystemHeader")
    private AquaplanetSystemHeader systemHeader;

    @JsonProperty("TransactionHeader")
    private AquaplanetTransactionHeader transactionHeader;

    @JsonProperty("MessageHeader")
    private AquaplanetMessageHeader messageHeader;

    @JsonProperty("Data")
    private T data;
}
