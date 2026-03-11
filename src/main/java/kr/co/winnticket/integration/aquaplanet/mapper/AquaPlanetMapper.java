package kr.co.winnticket.integration.aquaplanet.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface AquaPlanetMapper {

    void insertIssueHistory(Map<String, Object> param);

    void updateCancelHistory(Map<String, Object> param);

    Map<String, Object> selectIssueHistoryByOrderId(@Param("orderId") Long orderId);

    Map<String, Object> selectIssueHistoryByCouponNo(@Param("reprCponIndictNo") String reprCponIndictNo);
}