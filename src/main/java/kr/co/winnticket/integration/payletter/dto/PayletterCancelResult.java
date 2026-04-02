package kr.co.winnticket.integration.payletter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayletterCancelResult {

    private int cancelAmount;
    private int cancelFee;
    private PayletterCancelResDto pgResult;
}
