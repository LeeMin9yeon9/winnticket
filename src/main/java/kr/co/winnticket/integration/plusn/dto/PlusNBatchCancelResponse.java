package kr.co.winnticket.integration.plusn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlusNBatchCancelResponse {

    private boolean success;
    private String message;
    private List<String> canceledTickets;

    public static PlusNBatchCancelResponse success(List<String> list) {
        return PlusNBatchCancelResponse.builder()
                .success(true)
                .message("전체 취소 성공")
                .canceledTickets(list)
                .build();
    }

    public static PlusNBatchCancelResponse fail(String msg) {
        return PlusNBatchCancelResponse.builder()
                .success(false)
                .message(msg)
                .build();
    }
}
