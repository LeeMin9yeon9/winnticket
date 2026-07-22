package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * point_voucher 전체 컬럼 - 내부 처리(사용/차감)용, PII(customerName/benepiaId) 포함이라 API 응답으로 직접 노출하지 않음
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointVoucherDetailDto {
    private UUID id;
    private String voucherNumber;
    private String benepiaId;
    private String customerName;
    private String phone;
    private Integer totalAmount;
    private Integer usedAmount;
    private Integer remainingAmount;
    private String status;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private String pointTid;
    private UUID channelId;
    private Integer voucherCancelDays;
}
