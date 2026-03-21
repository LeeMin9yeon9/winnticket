package kr.co.winnticket.siteinfo.bankaccount.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[사이트 정보 > 은행 계좌 응답 DTO] BankAccountResponse")
public class BankAccountResDto {
    @Schema(description = "ID")
    private Long id;

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

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "생성자")
    private String createdBy;

    @Schema(description = "수정자")
    private String updatedBy;
}