package kr.co.winnticket.integration.benepia.pointVoucher.service;

import kr.co.winnticket.common.enums.VerificationPurpose;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointPayReqDto;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointPayResDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminDetailResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminListResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherDetailDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherExchangeReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherExchangeResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherLookupResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.mapper.PointVoucherMapper;
import kr.co.winnticket.channels.channel.dto.ChannelInfoResGetDto;
import kr.co.winnticket.channels.channel.mapper.ChannelMapper;
import kr.co.winnticket.sms.service.BizMsgService;
import kr.co.winnticket.sms.service.TemplateRenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PointVoucherService {

    // 인증 완료 후 이 시간 이내에만 전환 확정을 허용
    private static final int VERIFICATION_VALID_MINUTES = 30;

    // TODO: 이용권 유효기간 정책이 아직 확정되지 않아 임시로 발급일+12개월로 둠 - 정책 확정되면 교체 필요
    private static final int VOUCHER_VALID_MONTHS = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String DEFAULT_VOUCHER_SMS_TEMPLATE =
            "[윈앤티켓] {고객명}님의 이용권이 발급되었습니다.\n이용권번호: {이용권번호}\n금액: {이용권금액}원\n유효기간: {유효기간}까지";

    private final PointVoucherMapper mapper;
    private final KcpService kcpService;
    private final ChannelMapper channelMapper;
    private final BizMsgService bizMsgService;
    private final TemplateRenderService templateRenderService;

    public PointVoucherExchangeResDto exchange(PointVoucherExchangeReqDto req, String memcorpCd) {
        String phone = req.getPhone().replaceAll("[^0-9]", "");

        int verifiedCount = mapper.countRecentVerifiedPhone(
                phone,
                VerificationPurpose.POINT_EXCHANGE.name(),
                LocalDateTime.now().minusMinutes(VERIFICATION_VALID_MINUTES)
        );
        if (verifiedCount == 0) {
            throw new IllegalStateException("휴대폰 인증이 필요합니다.");
        }

        // KCP로 실제 베네피아 포인트 차감. 여기서 실패하면 이용권도 발급하지 않음
        String exchangeRef = "PVX" + System.currentTimeMillis();
        KcpPointPayReqDto payReq = KcpPointPayReqDto.builder()
                .orderNo(exchangeRef)
                .amount(req.getAmount())
                .benepiaId(req.getBenepiaId())
                .benepiaPwd(req.getBenepiaPwd())
                .memcorpCd(memcorpCd)
                .productName("포인트 이용권 구입")
                .productCode("POINT_VOUCHER_EXCHANGE")
                .buyerName(req.getCustomerName())
                .buyerPhone(phone)
                .build();

        KcpPointPayResDto payRes = kcpService.pointPay(payReq);
        if (!"0000".equals(payRes.getRes_cd())) {
            throw new IllegalStateException("포인트 차감 실패: " + payRes.getRes_msg());
        }
        log.info("[PointVoucher] KCP 포인트 차감 성공 benepiaId={} amount={} tno={}",
                req.getBenepiaId(), req.getAmount(), payRes.getTno());

        String voucherNumber = generateUniqueVoucherNumber();
        LocalDateTime validFrom = LocalDateTime.now();
        LocalDateTime validUntil = validFrom.plusMonths(VOUCHER_VALID_MONTHS);

        try {
            mapper.insertVoucher(
                    UUID.randomUUID(),
                    voucherNumber,
                    req.getBenepiaId(),
                    req.getCustomerName(),
                    phone,
                    req.getChannelId(),
                    req.getAmount(),
                    0,
                    req.getAmount(),
                    validFrom,
                    validUntil,
                    payRes.getTno()
            );
        } catch (Exception e) {
            // 이미 차감된 포인트를 그대로 두면 고객이 손해를 보므로, 이용권 발급 실패 시 차감을 되돌림
            log.error("[PointVoucher] 이용권 발급 실패 - 차감된 포인트 취소 시도 tno={}", payRes.getTno(), e);
            try {
                KcpPointCancelReqDto cancelReq = new KcpPointCancelReqDto();
                cancelReq.setTno(payRes.getTno());
                cancelReq.setOrderNo(exchangeRef);
                cancelReq.setCancelReason("이용권 발급 실패 자동 취소");
                kcpService.cancelPoint(cancelReq);
            } catch (Exception cancelEx) {
                log.error("[PointVoucher] 포인트 취소도 실패 - 수동 확인 필요 tno={}", payRes.getTno(), cancelEx);
            }
            throw new IllegalStateException("이용권 발급 중 오류가 발생했습니다.", e);
        }

        sendVoucherIssuedSms(req.getChannelId(), phone, req.getCustomerName(), voucherNumber, req.getAmount(), validUntil);

        return PointVoucherExchangeResDto.builder()
                .voucherNumber(voucherNumber)
                .totalAmount(req.getAmount())
                .validUntil(validUntil)
                .build();
    }

    // 이용권 조회 (베네피아 계정 불필요 - 이용권 번호만으로 조회)
    public PointVoucherLookupResDto lookup(String voucherNumber) {
        PointVoucherDetailDto voucher = mapper.findVoucherDetailByNumber(voucherNumber);
        if (voucher == null) {
            throw new IllegalArgumentException("존재하지 않는 이용권 번호입니다.");
        }

        return PointVoucherLookupResDto.builder()
                .voucherNumber(voucher.getVoucherNumber())
                .totalAmount(voucher.getTotalAmount())
                .usedAmount(voucher.getUsedAmount())
                .remainingAmount(voucher.getRemainingAmount())
                .status(voucher.getStatus())
                .validUntil(voucher.getValidUntil())
                .build();
    }

    // 이용권 사용(차감) - 결제 금액 일부/전액을 이용권으로 차감하고 사용이력을 남김
    @Transactional
    public PointVoucherLookupResDto redeem(String voucherNumber, int amount, String orderNumber) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액이 올바르지 않습니다.");
        }

        PointVoucherDetailDto voucher = mapper.findVoucherDetailByNumber(voucherNumber);
        if (voucher == null) {
            throw new IllegalArgumentException("존재하지 않는 이용권 번호입니다.");
        }
        if (!"ACTIVE".equals(voucher.getStatus())) {
            throw new IllegalStateException("사용할 수 없는 이용권입니다. (상태: " + voucher.getStatus() + ")");
        }
        if (voucher.getValidUntil() != null && voucher.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("사용기한이 지난 이용권입니다.");
        }
        if (voucher.getRemainingAmount() < amount) {
            throw new IllegalStateException("이용권 잔여금액이 부족합니다. (잔여: " + voucher.getRemainingAmount() + ")");
        }

        // 동시성 가드: remaining_amount >= amount일 때만 차감되므로, 0건이면 그 사이 다른 요청이 먼저 차감한 것
        int updated = mapper.deductVoucherAmount(voucher.getId(), amount);
        if (updated == 0) {
            throw new IllegalStateException("이용권 잔여금액이 부족합니다.");
        }

        int amountAfter = voucher.getRemainingAmount() - amount;

        mapper.insertVoucherUsage(
                UUID.randomUUID(),
                voucher.getId(),
                voucher.getVoucherNumber(),
                voucher.getCustomerName(),
                voucher.getBenepiaId(),
                orderNumber,
                voucher.getRemainingAmount(),
                amount,
                amountAfter
        );

        log.info("[PointVoucher] 이용권 사용 voucherNumber={} amount={} amountAfter={} orderNumber={}",
                voucherNumber, amount, amountAfter, orderNumber);

        return PointVoucherLookupResDto.builder()
                .voucherNumber(voucher.getVoucherNumber())
                .totalAmount(voucher.getTotalAmount())
                .usedAmount(voucher.getUsedAmount() + amount)
                .remainingAmount(amountAfter)
                .status(voucher.getStatus())
                .validUntil(voucher.getValidUntil())
                .build();
    }

    // 이용권 복원 (보상 처리용) - 카드 결제 실패 등으로 이미 차감한 이용권을 되돌릴 때
    @Transactional
    public void restore(String voucherNumber, int amount, String orderNumber) {
        PointVoucherDetailDto voucher = mapper.findVoucherDetailByNumber(voucherNumber);
        if (voucher == null) {
            log.error("[PointVoucher] 복원 대상 이용권을 찾을 수 없음 voucherNumber={}", voucherNumber);
            return;
        }

        mapper.restoreVoucherAmount(voucher.getId(), amount);
        mapper.deleteVoucherUsageByOrderNumber(voucher.getId(), orderNumber);

        log.info("[PointVoucher] 이용권 복원 완료 voucherNumber={} amount={} orderNumber={}",
                voucherNumber, amount, orderNumber);
    }

    // 관리자 이용권 취소 - 전혀 사용되지 않은 이용권만, 채널에 설정된 취소가능기간 이내에만 허용
    // 취소 성공 시 차감했던 포인트도 KCP로 전액 환불
    @Transactional
    public void cancelVoucher(UUID id) {
        PointVoucherDetailDto voucher = mapper.findVoucherDetailById(id);
        if (voucher == null) {
            throw new IllegalArgumentException("존재하지 않는 이용권입니다.");
        }
        if (!"ACTIVE".equals(voucher.getStatus())) {
            throw new IllegalStateException("취소할 수 없는 상태입니다. (상태: " + voucher.getStatus() + ")");
        }
        if (voucher.getUsedAmount() != null && voucher.getUsedAmount() > 0) {
            throw new IllegalStateException("이미 사용된 이용권은 취소할 수 없습니다.");
        }
        if (voucher.getVoucherCancelDays() == null) {
            throw new IllegalStateException("이 채널은 이용권 취소가 허용되지 않습니다.");
        }

        LocalDateTime deadline = voucher.getValidFrom().plusDays(voucher.getVoucherCancelDays());
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalStateException("이용권 변경(취소) 가능 기간이 지났습니다. (기한: " + deadline + ")");
        }

        if (voucher.getPointTid() == null || voucher.getPointTid().isBlank()) {
            log.error("[PointVoucher] 취소할 KCP 거래번호 없음 voucherId={}", id);
            throw new IllegalStateException("포인트 차감 이력을 찾을 수 없어 취소할 수 없습니다. 관리자에게 문의해주세요.");
        }

        KcpPointCancelReqDto cancelReq = new KcpPointCancelReqDto();
        cancelReq.setTno(voucher.getPointTid());
        cancelReq.setCancelReason("관리자 이용권 취소");
        kcpService.cancelPoint(cancelReq); // 실패 시 내부에서 예외 발생 - 이용권 상태는 그대로 유지됨

        mapper.updateVoucherStatus(id, "CANCELLED");

        log.info("[PointVoucher] 관리자 취소 완료 voucherId={} voucherNumber={} pointTid={}",
                id, voucher.getVoucherNumber(), voucher.getPointTid());
    }

    // 관리자 이용권 목록 조회
    public List<PointVoucherAdminListResDto> adminList(String keyword, String status) {
        return mapper.selectVoucherAdminList(keyword, status);
    }

    // 관리자 이용권 상세 조회 (사용내역 포함)
    public PointVoucherAdminDetailResDto adminDetail(UUID id) {
        PointVoucherAdminDetailResDto detail = mapper.selectVoucherAdminDetail(id);
        if (detail == null) {
            throw new IllegalArgumentException("존재하지 않는 이용권입니다.");
        }
        detail.setUsages(mapper.selectVoucherUsageList(id));
        return detail;
    }

    // 이용권 발급 완료 SMS 발송 - 실패해도 이용권 발급 자체는 이미 끝난 상태이므로 예외를 던지지 않고 로그만 남김
    private void sendVoucherIssuedSms(UUID channelId, String phone, String customerName,
                                       String voucherNumber, int amount, LocalDateTime validUntil) {
        try {
            ChannelInfoResGetDto channel = channelMapper.selectChannel(channelId);
            String template = (channel != null && channel.getVoucherSmsTemplate() != null
                    && !channel.getVoucherSmsTemplate().isBlank())
                    ? channel.getVoucherSmsTemplate()
                    : DEFAULT_VOUCHER_SMS_TEMPLATE;

            String message = templateRenderService.render(template, Map.of(
                    "고객명", customerName,
                    "이용권번호", voucherNumber,
                    "이용권금액", String.valueOf(amount),
                    "유효기간", validUntil.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ));

            String cmid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            bizMsgService.sendSms(cmid, phone, customerName, "0415455681", "윈앤티켓", message);
        } catch (Exception e) {
            log.error("[PointVoucher] 이용권 발급 SMS 발송 실패 voucherNumber={}", voucherNumber, e);
        }
    }

    private String generateUniqueVoucherNumber() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder(16);
            for (int i = 0; i < 16; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            String candidate = sb.toString();
            if (mapper.findVoucherIdByNumber(candidate) == null) {
                return candidate;
            }
        }
        throw new IllegalStateException("이용권 번호 생성 실패 (재시도 초과)");
    }
}
