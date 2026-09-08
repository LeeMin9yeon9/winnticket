package kr.co.winnticket.ticketCoupon.mapper;

import kr.co.winnticket.ticketCoupon.dto.TicketCouponGroupResDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponListResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TicketCouponMapper {

    // 그룹 생성
    void insertGroup(
            @Param("groupId") UUID groupId,
            @Param("productId") UUID productId,
            @Param("productOptionId") UUID productOptionId,
            @Param("productOptionValueId") UUID productOptionValueId,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    // 쿠폰 생성
    void insertCoupon(
            @Param("groupId") UUID groupId,
            @Param("couponNumber") String couponNumber,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    // 그룹 목록 조회
    List<TicketCouponGroupResDto> selectGroups(@Param("productId") UUID productId);

    // 상품별 그룹 목록 조회
    List<TicketCouponGroupResDto> selectGroupsByProductId(@Param("productId") UUID productId);

    // 그룹 단건 조회
    TicketCouponGroupResDto selectGroup(@Param("groupId") UUID groupId);

    // 그룹별 쿠폰 목록 조회
    List<TicketCouponListResDto> selectCouponsByGroup(@Param("groupId") UUID groupId);

    // 쿠폰 단건 조회
    TicketCouponListResDto selectCoupon(@Param("couponId") UUID couponId);

    // 쿠폰 수정(번호/상태/사용일자/유효기간)
    void updateCoupon(
            @Param("couponId") UUID couponId,
            @Param("couponNumber") String couponNumber,
            @Param("status") String status,
            @Param("usedAt") LocalDateTime usedAt,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    // 쿠폰 삭제
    void deleteCoupon(@Param("couponId") UUID couponId);

    // 그룹 수정(그룹명/유효기간)
    void updateGroup(
            @Param("groupId") UUID groupId,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    // 그룹 내 쿠폰 전체 삭제 (그룹 삭제 전 호출)
    void deleteCouponsByGroupId(@Param("groupId") UUID groupId);

    // 그룹 삭제
    void deleteGroup(@Param("groupId") UUID groupId);

    // 겹치는 날짜 그룹 체크
    int countOverlappingGroups(
            @Param("productOptionValueId") UUID productOptionValueId,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    //  쿠폰번호 중복 체크용
    UUID findCouponIdByCouponNumber(@Param("couponNumber") String couponNumber);


    // 쿠폰 판매처리
    void markCouponSold(@Param("couponId") UUID couponId);

    // 판매 상태 획인(복구용)
    String findCouponStatus(@Param("couponId") UUID couponId);

    // 판매티켓 미사용 시 복구
    int restoreCoupon(@Param("couponId") UUID couponId);

    // 그룹별 티켓 날짜 일괄 변경
    void updateGroupDate(@Param("groupId") UUID groupId,
                         @Param("validFrom") LocalDate validFrom,
                         @Param("validUntil") LocalDate validUntil);

    // 그룹에 속한 개별 쿠폰들의 날짜 일괄 변경
    void updateCouponsDateByGroupId(@Param("groupId") UUID groupId,
                                    @Param("validFrom") LocalDate validFrom,
                                    @Param("validUntil") LocalDate validUntil);

    // 쿠폰 하나가 이미 주문에 발급된 상태(order_tickets)라면, 그 발급본의 유효기간도
    // 방금 바뀐 쿠폰 유효기간으로 같이 동기화 - 안 해주면 관리자취소 가능여부 판단이
    // 여전히 옛날 유효기간을 봐서 취소 버튼이 계속 비활성화됨
    void syncOrderTicketValidityByCouponId(@Param("couponId") UUID couponId);

    // 그룹 전체 날짜 변경 시, 그 그룹에 속한 쿠폰들 중 이미 발급된 것도 전부 동기화
    void syncOrderTicketValidityByGroupId(@Param("groupId") UUID groupId);

    // 유효기간 다르면 다른 그룹 생성
    UUID findGroupByOptionValueAndDate(
            @Param("productOptionValueId") UUID productOptionValueId,
            @Param("validFrom") LocalDate validFrom,
            @Param("validUntil") LocalDate validUntil
    );

    // 유효기간 빠른 쿠폰 먼저 사용
    TicketCouponListResDto findActiveCouponByOptionValueId(UUID optionValueId);

    // 옵션값별 활성 쿠폰 수량 조회
    int countActiveCouponsByOptionValueId(@Param("optionValueId") UUID optionValueId);

    // 쿠폰 예약 처리 (ACTIVE → PENDING)
    int markCouponPending(@Param("couponId") UUID couponId);

    // 예약 쿠폰 복구 (PENDING → ACTIVE)
    int restorePendingCoupon(@Param("couponId") UUID couponId);

    // 예약 쿠폰 판매 확정 (PENDING → SOLD)
    int markPendingCouponSold(@Param("couponId") UUID couponId);

}
