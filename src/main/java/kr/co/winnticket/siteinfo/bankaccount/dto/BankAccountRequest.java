package kr.co.winnticket.siteinfo.bankaccount.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountRequest {
    private String bankName;
    private String accountNumber;
    private String accountHolder;
    private Boolean visible;
    private Integer displayOrder;
}
