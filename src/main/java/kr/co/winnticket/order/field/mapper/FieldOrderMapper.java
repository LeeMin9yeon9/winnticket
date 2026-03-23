package kr.co.winnticket.order.field.mapper;

import kr.co.winnticket.order.field.dto.FieldOrderListGetResDto;
import kr.co.winnticket.order.field.dto.FieldOrderStatusGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface FieldOrderMapper {

    List<FieldOrderListGetResDto> selectFieldOrderList(
            @Param("partnerId") UUID partnerId,
            @Param("srchWord") String srchWord,
            @Param("begDate") LocalDate begDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status
    );

    FieldOrderStatusGetResDto selectFieldOrderStatus(
            @Param("partnerId") UUID partnerId,
            @Param("begDate") LocalDate begDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status
    );
}
