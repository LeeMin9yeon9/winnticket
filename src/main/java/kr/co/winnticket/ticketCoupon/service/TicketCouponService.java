package kr.co.winnticket.ticketCoupon.service;

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


     //선사입형 쿠폰 생성
     @Transactional
     public void createCoupons(TicketCouponCreateReqDto dto){

         Boolean prePurchased = productMapper.selectPrePurchasedByProductId(dto.getProductId());


         if(prePurchased == null){
             throw new RuntimeException("상품이 존재하지 않습니다.");
         }


         if(!prePurchased){
             throw new RuntimeException("선사입형 상품만 쿠폰 생성 가능");
         }

         UUID groupId = mapper.findGroupByOptionValueId(dto.getProductOptionValueId());

         if(groupId == null){
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

         createCouponsByRange(
                 groupId,
                 dto.getStartNumber(),
                 dto.getEndNumber(),
                 dto.getValidFrom(),
                 dto.getValidUntil()

         );
     }

    // 쿠폰번호 범위 생성
    private void createCouponsByRange(UUID groupId, String start, String end, LocalDate validFrom, LocalDate validUntil) {

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
                log.error("[쿠폰생성 실패] 중복 쿠폰번호 발견: {}", couponNumber);

                throw new RuntimeException(
                        "이미 존재하는 쿠폰번호: " + couponNumber
                );
            }

            mapper.insertCoupon(groupId, couponNumber, validFrom, validUntil);

            // 쿠폰 생성 성공 시 증가
            createdCount++;

            log.info("[쿠폰생성 완료] groupId={}, 생성개수={}", groupId, createdCount
            );
        }
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
    public List<TicketCouponGroupResDto> getGroups() {
        return mapper.selectGroups();
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

    // 쿠폰 수정(번호/상태/사용일자/유효기간)
    @Transactional
    public void updateCoupon(UUID couponId, TicketCouponUpdateReqDto dto) {

        if (dto.getCouponNumber() != null && !dto.getCouponNumber().isBlank()) {
            UUID existsId = mapper.findCouponIdByCouponNumber(dto.getCouponNumber());
            if (existsId != null && !existsId.equals(couponId)) {
                throw new RuntimeException("이미 존재하는 쿠폰번호입니다.");
            }
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

    // 그룹 수정(유효기간)
    @Transactional
    public void updateGroup(UUID groupId, LocalDate validFrom, LocalDate validUntil) {
        mapper.updateGroup(groupId, validFrom, validUntil);
    }

    // 그룹 삭제
    @Transactional
    public void deleteGroup(UUID groupId) {
        mapper.deleteGroup(groupId);
    }

    // 쿠폰 주문 발급
    @Transactional
    public String issueCoupon(UUID orderItemId) {

        UUID orderId = orderMapper.findOrderIdByOrderItemId(orderItemId);

        // 상품ID 조회 추가
        UUID productId = orderMapper.findProductIdByOrderItemId(orderItemId);

        // 옵션값 조회
        UUID optionValueId = orderMapper.findOptionValueIdByOrderItem(orderItemId);

        // 그룹 조회
        UUID groupId = mapper.findGroupByOptionValueId(optionValueId);

        // 쿠폰 조회
        TicketCouponListResDto coupon =
                mapper.findActiveCoupon(groupId);

        if (coupon == null) {
            throw new RuntimeException("쿠폰 재고 없음");
        }

        // SOLD 처리
        mapper.markCouponSold(coupon.getId());

        productMapper.decreaseStock(optionValueId);

        // 주문 쿠폰 연결
        orderMapper.insertOrderItemCoupon(
                orderId,
                orderItemId,
                productId,
                optionValueId,
                coupon.getId(),
                coupon.getCouponNumber()
        );

        // 티켓 생성
        orderMapper.insertOrderTicket(
                orderItemId,
                coupon.getCouponNumber()
        );

        return coupon.getCouponNumber();
    }

    // 판매된 티켓 조회 후 미사용 시 복구
    @Transactional
    public void cancelCoupon(UUID couponId){

        String status = mapper.findCouponStatus(couponId);

        if(status.equals("USED")){
            throw new RuntimeException("이미 사용된 쿠폰은 취소 불가");
        }

        if(status.equals("SOLD")){
            mapper.restoreCoupon(couponId);
        }
    }
}
