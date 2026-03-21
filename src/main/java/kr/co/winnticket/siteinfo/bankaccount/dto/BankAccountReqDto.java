package kr.co.winnticket.siteinfo.bankaccount.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[사이트 정보 > 은행 계좌 요청 DTO] BankAccountRequest")
public class BankAccountReqDto {
    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "계좌번호")
    private String accountNumber;

    @Schema(description = "예금주")
    private String accountHolder;

    @Schema(description = "노출 여부")
    private Boolean visible;

    @Schema(description = "표시 순서")
    private Integer displayOrder;
}
