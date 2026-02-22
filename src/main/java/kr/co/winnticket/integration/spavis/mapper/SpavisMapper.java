package kr.co.winnticket.integration.spavis.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface SpavisMapper {

    // 이용결과 확인
    String selectCouponNo(UUID orderId);
}
