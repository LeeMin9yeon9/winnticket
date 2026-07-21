package kr.co.winnticket.integration.benepia.pointVoucher.mapper;

import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherDetailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface PointVoucherMapper {

    // 기존 미인증 코드 무효화 (만료 처리) - 재발송 시 이전 코드가 계속 유효하지 않도록
    void invalidatePendingVerifications(@Param("phone") String phone, @Param("purpose") String purpose);

    // 인증번호 저장
    void insertPhoneVerification(
            @Param("id") UUID id,
            @Param("phone") String phone,
            @Param("code") String code,
            @Param("purpose") String purpose,
            @Param("expiresAt") LocalDateTime expiresAt,
            @Param("customerName") String customerName,
            @Param("benepiaId") String benepiaId
    );

    // 유효한(미인증, 미만료, 코드일치) 인증 레코드 조회
    UUID findValidVerificationId(
            @Param("phone") String phone,
            @Param("purpose") String purpose,
            @Param("code") String code
    );

    // 인증 완료 처리
    void markVerificationVerified(@Param("id") UUID id);

    // 최근(since 이후) 인증완료 이력 존재 여부 확인용 카운트
    int countRecentVerifiedPhone(
            @Param("phone") String phone,
            @Param("purpose") String purpose,
            @Param("since") LocalDateTime since
    );

    // 이용권번호 중복 체크용
    UUID findVoucherIdByNumber(@Param("voucherNumber") String voucherNumber);

    // 이용권 발급
    void insertVoucher(
            @Param("id") UUID id,
            @Param("voucherNumber") String voucherNumber,
            @Param("benepiaId") String benepiaId,
            @Param("customerName") String customerName,
            @Param("phone") String phone,
            @Param("channelId") UUID channelId,
            @Param("totalAmount") int totalAmount,
            @Param("usedAmount") int usedAmount,
            @Param("remainingAmount") int remainingAmount,
            @Param("validFrom") LocalDateTime validFrom,
            @Param("validUntil") LocalDateTime validUntil
    );

    // 이용권 번호로 상세 조회 (사용/차감 처리용, PII 포함)
    PointVoucherDetailDto findVoucherDetailByNumber(@Param("voucherNumber") String voucherNumber);

    // 이용권 차감 (remaining_amount가 충분할 때만 적용되는 동시성 가드 포함, 적용된 row 수 반환)
    int deductVoucherAmount(
            @Param("id") UUID id,
            @Param("amount") int amount
    );

    // 이용권 복원 (카드 결제 실패 등 보상 처리 시 차감을 되돌림)
    void restoreVoucherAmount(@Param("id") UUID id, @Param("amount") int amount);

    // 특정 주문에 대한 사용이력 삭제 (보상 처리 시 사용이력도 되돌림)
    void deleteVoucherUsageByOrderNumber(@Param("voucherId") UUID voucherId, @Param("orderNumber") String orderNumber);

    // 이용권 사용이력 기록
    void insertVoucherUsage(
            @Param("id") UUID id,
            @Param("voucherId") UUID voucherId,
            @Param("voucherNumber") String voucherNumber,
            @Param("customerName") String customerName,
            @Param("benepiaId") String benepiaId,
            @Param("orderNumber") String orderNumber,
            @Param("amountBefore") int amountBefore,
            @Param("usedAmount") int usedAmount,
            @Param("amountAfter") int amountAfter
    );
}
