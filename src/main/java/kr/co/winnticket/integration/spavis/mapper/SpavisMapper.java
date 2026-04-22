package kr.co.winnticket.integration.spavis.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface SpavisMapper {

    // 이용결과 확인
    List<String> selectCouponNo(UUID orderId);

}
