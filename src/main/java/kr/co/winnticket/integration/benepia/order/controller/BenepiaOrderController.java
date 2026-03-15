package kr.co.winnticket.integration.benepia.order.controller;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaCancelRequest;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaOrderRequest;
import kr.co.winnticket.integration.benepia.order.service.BenepiaOrderService;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/benepia/orders")
@Log4j2
public class BenepiaOrderController {

    private final BenepiaOrderService service;
    private final OrderMapper orderMapper;

    @GetMapping("/order/{orderId}")
    public void testOrder(@PathVariable UUID orderId){

        OrderAdminDetailGetResDto order =
                orderMapper.selectOrderAdminDetail(orderId);

        List<OrderProductListGetResDto> items =
                orderMapper.selectOrderProductList(orderId);

        // 테스트용 bene 객체
        BenepiaDecryptedParamDto bene = new BenepiaDecryptedParamDto();
        bene.setBenefit_id("testtravel");
        bene.setSitecode("5555");

    }
    @GetMapping("/cancle/{orderId}")
    public void testCancle(@PathVariable UUID orderId){

        OrderAdminDetailGetResDto order =
                orderMapper.selectOrderAdminDetail(orderId);

        List<OrderProductListGetResDto> items =
                orderMapper.selectOrderProductList(orderId);

        // 테스트용 bene 객체
        BenepiaDecryptedParamDto bene = new BenepiaDecryptedParamDto();
        bene.setBenefit_id("testtravel");
        bene.setSitecode("5555");

    }
}