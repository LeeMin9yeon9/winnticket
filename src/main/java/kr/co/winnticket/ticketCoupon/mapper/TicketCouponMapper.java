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

    // 옵션값 기준 그룹 존재 여부 확인
    UUID findGroupByOptionValueId(@Param("productOptionValueId") UUID productOptionValueId);

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
    List<TicketCouponGroupResDto> selectGroups();

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

    // 그룹 삭제(그룹 삭제하면 쿠폰도 CASCADE 라면 같이 삭제됨)
    void deleteGroup(@Param("groupId") UUID groupId);

    //  쿠폰번호 중복 체크용
    UUID findCouponIdByCouponNumber(@Param("couponNumber") String couponNumber);

    // 사용 가능한 쿠폰 1개 조회
    TicketCouponListResDto findActiveCoupon(@Param("groupId") UUID groupId);

    // 쿠폰 판매처리
    void markCouponSold(@Param("couponId") UUID couponId);

    // 판매 상태 획인(복구용)
    String findCouponStatus(@Param("couponId") UUID couponId);

    // 판매티켓 미사용 시 복구
    void restoreCoupon(@Param("couponId")UUID couponId);


}
