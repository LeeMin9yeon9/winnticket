package kr.co.winnticket.order.admin.service;

import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.order.admin.dto.OrderAdminListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminStatusGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;

    // 주문 상태 조회
    public OrderAdminStatusGetResDto selectOrderAdminStatus() {
        OrderAdminStatusGetResDto model = mapper.selectOrderAdminStatus();
        return model;
    }

    // 주문 목록 조회 (관리자)
    public List<OrderAdminListGetResDto> selectOrderAdminList(String asSrchWord, LocalDate asBegDate, LocalDate asEndDate, String status) {
        List<OrderAdminListGetResDto> lModel = mapper.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, status);
        return lModel;
    }
}
