package kr.co.winnticket.order.service;

import kr.co.winnticket.order.dto.OrderStatusCountGetResDto;
import kr.co.winnticket.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;

    public OrderStatusCountGetResDto selectOrderStatusCount() {
        OrderStatusCountGetResDto model = mapper.selectOrderStatusCount();
        return model;
    }
}
