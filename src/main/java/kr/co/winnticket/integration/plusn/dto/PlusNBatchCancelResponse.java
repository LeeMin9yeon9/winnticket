package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlusNBatchCancelResponse {
    private String orderId;
    private List<PlusNCancelResponse> results;
}
