package kr.co.winnticket.order.field.service;

import kr.co.winnticket.order.field.dto.FieldOrderListGetResDto;
import kr.co.winnticket.order.field.dto.FieldOrderStatusGetResDto;
import kr.co.winnticket.order.field.mapper.FieldOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldOrderService {

    private final FieldOrderMapper fieldOrderMapper;

    public List<FieldOrderListGetResDto> selectFieldOrderList(
            UUID partnerId, String srchWord, LocalDate begDate, LocalDate endDate, String status) {
        return fieldOrderMapper.selectFieldOrderList(partnerId, srchWord, begDate, endDate, status);
    }

    public FieldOrderStatusGetResDto selectFieldOrderStatus(
            UUID partnerId, LocalDate begDate, LocalDate endDate, String status) {
        return fieldOrderMapper.selectFieldOrderStatus(partnerId, begDate, endDate, status);
    }
}
