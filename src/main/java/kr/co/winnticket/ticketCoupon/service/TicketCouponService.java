package kr.co.winnticket.ticketCoupon.service;

import kr.co.winnticket.order.admin.dto.OrderItemCouponDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponCreateReqDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponGroupResDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponListResDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponUpdateReqDto;
import kr.co.winnticket.ticketCoupon.mapper.TicketCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TicketCouponService {

    private final TicketCouponMapper mapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;


    // 선사입형 쿠폰 생성 - 결과 메시지 반환
    @Transactional
    public String createCoupons(TicketCouponCreateReqDto dto) {

        Boolean prePurchased = productMapper.selectPrePurchasedByProductId(dto.getProductId());

        if (prePurchased == null) {
            throw new RuntimeException("상품이 존재하지 않습니다.");
        }

        if (!prePurchased) {
            throw new RuntimeException("선사입형 상품만 쿠폰 생성 가능");
        }

        //  날짜 검증
        validateDates(dto.getValidFrom(), dto.getValidUntil());

        //  동일 날짜 그룹 먼저 확인 (있으면 재사용)
        UUID groupId = mapper.findGroupByOptionValueAndDate(
                dto.getProductOptionValueId(),
                dto.getValidFrom(),
                dto.getValidUntil()
        );

        if (groupId == null) {
            //  겹치는 날짜 그룹 체크 (다른 날짜 범위와 겹치는 경우만)
            int overlapCount = mapper.countOverlappingGroups(
                    dto.getProductOptionValueId(),
                    dto.getValidFrom(),
                    dto.getValidUntil()
            );
            if (overlapCount > 0) {
                throw new RuntimeException("해당 옵션에 겹치는 유효기간의 쿠폰그룹이 이미 존재합니다.");
            }

            groupId = UUID.randomUUID();

            mapper.insertGroup(
                    groupId,
                    dto.getProductId(),
                    dto.getProductOptionId(),
                    dto.getProductOptionValueId(),
                    dto.getValidFrom(),
                    dto.getValidUntil()
            );
        }

        return createCouponsByRange(
                groupId,
                dto.getStartNumber(),
                dto.getEndNumber(),
                dto.getValidFrom(),
                dto.getValidUntil()
        );

    }

    //  쿠폰번호 범위 생성 - 중복 시 skip하고 결과 메시지 반환
    private String createCouponsByRange(UUID groupId, String start, String end, LocalDate validFrom, LocalDate validUntil) {

        RangeParts s = RangeParts.parse(start);
        RangeParts e = RangeParts.parse(end);

        if (!s.prefix.equals(e.prefix)) throw new RuntimeException("시작/끝 쿠폰 prefix가 다릅니다.");
        if (s.number > e.number) throw new RuntimeException("시작번호가 끝번호보다 큽니다.");
        if (s.width != e.width) throw new RuntimeException("시작/끝 쿠폰 숫자자리수가 다릅니다.");

        List<String> duplicatedCoupons = new ArrayList<>();
        int createdCount = 0;

        for (long i = s.number; i <= e.number; i++) {
            String couponNumber = (s.prefix + String.format("%0" + s.width + "d", i)).trim();

            UUID exists = mapper.findCouponIdByCouponNumber(couponNumber);

            if (exists != null) {
                duplicatedCoupons.add(couponNumber);
                log.warn("[쿠폰생성] 중복 쿠폰번호 건너뜀: {}", couponNumber);
                continue; // 중복은 건너뛰고 계속 생성
            }

            mapper.insertCoupon(groupId, couponNumber, validFrom, validUntil);
            createdCount++;
        }

        log.info("[쿠폰생성 완료] groupId={}, 생성={}건, 중복건너뜀={}건", groupId, createdCount, duplicatedCoupons.size());

        if (createdCount == 0 && !duplicatedCoupons.isEmpty()) {
            throw new RuntimeException("모든 쿠폰번호가 이미 존재합니다. 중복: " + String.join(", ", duplicatedCoupons));
        }

        if (!duplicatedCoupons.isEmpty()) {
            return createdCount + "건 생성 완료. 중복으로 건너뛴 번호: " + String.join(", ", duplicatedCoupons);
        }

        return createdCount + "건 생성 완료";
    }

    private static class RangeParts {
        final String prefix;
        final long number;
        final int width;

        private RangeParts(String prefix, long number, int width) {
            this.prefix = prefix;
            this.number = number;
            this.width = width;
        }

        static RangeParts parse(String value) {
            String prefix = value.replaceAll("[0-9]", "");
            String digits = value.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) throw new RuntimeException("쿠폰번호 숫자부가 없습니다.");
            return new RangeParts(prefix, Long.parseLong(digits), digits.length());
        }
    }


    // 그룹 목록
    @Transactional(readOnly = true)
    public List<TicketCouponGroupResDto> getGroups(UUID productId) {

        List<TicketCouponGroupResDto> groups = mapper.selectGroups(productId);

        for (TicketCouponGroupResDto group : groups) {
            List<TicketCouponListResDto> coupons =
                    mapper.selectCouponsByGroup(group.getId());

            group.setCoupons(coupons);
        }

        return groups;
    }

    // 그룹 단건
    @Transactional(readOnly = true)
    public TicketCouponGroupResDto getGroup(UUID groupId) {
        return mapper.selectGroup(groupId);
    }

    // 그룹별 쿠폰 목록
    @Transactional(readOnly = true)
    public List<TicketCouponListResDto> getCouponsByGroup(UUID groupId) {
        return mapper.selectCouponsByGroup(groupId);
    }

    // 쿠폰 단건
    @Transactional(readOnly = true)
    public TicketCouponListResDto getCoupon(UUID couponId) {
        return mapper.selectCoupon(couponId);
    }

    //  쿠폰 수정 - 에러 메시지 개선
    @Transactional
    public void updateCoupon(UUID couponId, TicketCouponUpdateReqDto dto) {

        if (dto.getCouponNumber() != null && !dto.getCouponNumber().isBlank()) {
            UUID existsId = mapper.findCouponIdByCouponNumber(dto.getCouponNumber());
            if (existsId != null && !existsId.equals(couponId)) {
                throw new IllegalArgumentException("이미 존재하는 쿠폰번호입니다: " + dto.getCouponNumber());
            }
        }

        // [이슈6] 날짜 검증
        if (dto.getValidFrom() != null && dto.getValidUntil() != null) {
            validateDates(dto.getValidFrom(), dto.getValidUntil());
        }

        mapper.updateCoupon(
                couponId,
                dto.getCouponNumber(),
                dto.getStatus(),
                dto.getUsedAt(),
                dto.getValidFrom(),
                dto.getValidUntil()
        );
    }


    // 쿠폰 삭제
    @Transactional
    public void deleteCoupon(UUID couponId) {
        mapper.deleteCoupon(couponId);
    }

    // [이슈6] 그룹 수정 - 날짜 검증 추가
    @Transactional
    public void updateGroup(UUID groupId, LocalDate validFrom, LocalDate validUntil) {
        if (validFrom != null && validUntil != null) {
            validateDates(validFrom, validUntil);
        }
        mapper.updateGroup(groupId, validFrom, validUntil);
    }

    // [이슈3] 그룹 삭제 - 하위 쿠폰 먼저 삭제
    @Transactional
    public void deleteGroup(UUID groupId) {
        mapper.deleteCouponsByGroupId(groupId); // 하위 쿠폰 먼저 삭제
        mapper.deleteGroup(groupId);
    }

    // 선사입 쿠폰 예약 (주문 생성 시점)
    // ACTIVE 쿠폰 N개를 PENDING으로 변경하고 order_item_coupons 에 기록
    @Transactional
    public void reserveCoupons(UUID orderId, UUID orderItemId, UUID productId, UUID optionValueId, int quantity) {

        for (int i = 0; i < quantity; i++) {

            TicketCouponListResDto coupon = mapper.findActiveCouponByOptionValueId(optionValueId);

            if (coupon == null) {
                throw new IllegalArgumentException("선사입 쿠폰 재고가 부족합니다.");
            }

            int updated = mapper.markCouponPending(coupon.getId());

            if (updated == 0) {
                throw new IllegalStateException("쿠폰 예약 실패 (동시성 충돌)");
            }

            orderMapper.insertOrderItemCoupon(
                    orderId,
                    orderItemId,
                    productId,
                    optionValueId,
                    coupon.getId(),
                    coupon.getCouponNumber()
            );

            log.info("[쿠폰 예약] orderItemId={}, couponId={}, couponNumber={}",
                    orderItemId, coupon.getId(), coupon.getCouponNumber());
        }
    }

    // 쿠폰 주문 발급
    @Transactional
    public String issueCoupon(UUID orderItemId, LocalDate validFrom, LocalDate validTo) {

        log.info("[쿠폰발급 시작] orderItemId={}", orderItemId);

        UUID orderId = orderMapper.findOrderIdByOrderItemId(orderItemId);
        UUID productId = orderMapper.findProductIdByOrderItemId(orderItemId);
        UUID optionValueId = orderMapper.findOptionValueIdByOrderItem(orderItemId);

        TicketCouponListResDto coupon = mapper.findActiveCouponByOptionValueId(optionValueId);

        if (coupon == null) {
            throw new RuntimeException("쿠폰 재고 없음");
        }

        mapper.markCouponSold(coupon.getId());

        orderMapper.insertOrderItemCoupon(
                orderId,
                orderItemId,
                productId,
                optionValueId,
                coupon.getId(),
                coupon.getCouponNumber()
        );

        String ticketNumber = coupon.getCouponNumber();

        UUID existingId = orderMapper.findCancelledTicket(ticketNumber);

        if (existingId != null) {

            // 재사용 (핵심)
            orderMapper.reuseTicket(
                    existingId,
                    orderItemId,
                    validFrom,
                    validTo
            );

            log.info("[TICKET REUSE] {}", ticketNumber);

        } else {

            orderMapper.insertOrderTicket(
                    orderItemId,
                    coupon.getCouponNumber(),
                    validFrom,
                    validTo
            );

            log.info("[쿠폰발급] orderId={}, orderItemId={}, productId={}, optionValueId={}, coupon={}",
                    orderId, orderItemId, productId, optionValueId, coupon.getCouponNumber());
        }
            return coupon.getCouponNumber();
        }


    // 예약된(PENDING) 쿠폰을 판매 확정(SOLD)하고 티켓 발행
    // 결제 완료 시점에 호출됨. 수량만큼 이미 예약된 쿠폰을 모두 확정한다.
    @Transactional
    public List<String> issueReservedCoupons(UUID orderItemId, LocalDate validFrom, LocalDate validTo) {

        List<OrderItemCouponDto> reserved = orderMapper.selectOrderItemCouponsByOrderItemId(orderItemId);

        if (reserved == null || reserved.isEmpty()) {
            throw new RuntimeException("예약된 쿠폰이 없습니다. orderItemId=" + orderItemId);
        }

        List<String> ticketNumbers = new ArrayList<>();

        for (OrderItemCouponDto c : reserved) {

            int updated = mapper.markPendingCouponSold(c.getTicketCouponId());

            if (updated == 0) {
                throw new IllegalStateException("예약 쿠폰 판매 확정 실패 couponId=" + c.getTicketCouponId());
            }

            String ticketNumber = c.getCouponNumber();

            UUID existingId = orderMapper.findCancelledTicket(ticketNumber);

            if (existingId != null) {
                orderMapper.reuseTicket(existingId, orderItemId, validFrom, validTo);
                log.info("[TICKET REUSE] {}", ticketNumber);
            } else {
                orderMapper.insertOrderTicket(orderItemId, ticketNumber, validFrom, validTo);
                log.info("[쿠폰 판매확정/티켓발행] orderItemId={}, couponNumber={}", orderItemId, ticketNumber);
            }

            ticketNumbers.add(ticketNumber);
        }

        return ticketNumbers;
    }

    // 예약된(PENDING) 쿠폰을 복구(ACTIVE)하고 order_item_coupons 삭제
    // 입금 전 주문 취소, 주문 만료 시 호출됨
    @Transactional
    public void restoreReservedCoupons(UUID orderId) {

        List<UUID> reservedIds = orderMapper.selectPrePurchasedCouponIds(orderId);

        for (UUID couponId : reservedIds) {
            int restored = mapper.restorePendingCoupon(couponId);
            if (restored == 0) {
                log.warn("[쿠폰 복구 실패] PENDING 상태가 아님 couponId={}", couponId);
            }
        }

        orderMapper.deleteOrderItemCouponsByOrderId(orderId);

        log.info("[RESERVED COUPON RESTORE] 완료 orderId={}, count={}", orderId, reservedIds.size());
    }

    // 판매된 티켓 조회 후 미사용 시 복구
    @Transactional
    public void cancelCoupon(UUID couponId) {

        String status = mapper.findCouponStatus(couponId);

        if (status == null) {
            throw new RuntimeException("존재하지 않는 쿠폰입니다.");
        }

        if (status.equals("USED")) {
            throw new RuntimeException("이미 사용된 쿠폰은 취소 불가");
        }

        if (status.equals("SOLD")) {
            mapper.restoreCoupon(couponId);
        }
    }

    // [이슈6] 쿠폰 그룹 날짜 일괄 변경 - 날짜 검증 추가
    @Transactional
    public void updateGroupDate(UUID groupId, LocalDate validFrom, LocalDate validUntil) {
        validateDates(validFrom, validUntil);
        mapper.updateGroupDate(groupId, validFrom, validUntil);
        mapper.updateCouponsDateByGroupId(groupId, validFrom, validUntil);
    }

    // [이슈6] 공통 날짜 검증
    private void validateDates(LocalDate validFrom, LocalDate validUntil) {
        if (validFrom == null || validUntil == null) {
            throw new RuntimeException("유효기간 시작일과 종료일은 필수입니다.");
        }
        if (validFrom.isAfter(validUntil)) {
            throw new RuntimeException("유효기간 시작일이 종료일보다 늦을 수 없습니다.");
        }
    }
}
