package kr.co.winnticket.integration.smartinfini.controller;

import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.service.SmartInfiniService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/smartInfini/test")
public class SmartInfiniController {

    private final SmartInfiniService service;

    /**
     * SmartInfini 사용처리 콜백 (문서상 POST JSON)
     */
    @PostMapping(value = "/use/callback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SIUseCallbackResponse useCallback(@RequestBody SIUseCallbackRequest req) {
        return service.onUseCallback(req);
    }

    /**
     * (호환) GET 쿼리스트링으로 들어오는 케이스도 같이 허용
     * 예: /api/smartinfini/use/callback?order_div=...&ticket_code=...&result_date=...
     */
    @GetMapping(value = "/use/callback", produces = MediaType.APPLICATION_JSON_VALUE)
    public SIUseCallbackResponse useCallbackGet(SIUseCallbackRequest req) {
        return service.onUseCallback(req);
    }

    /**
     * 주문 테스트
     */
    @PostMapping("/order")
    public SIOrderResponse order(
            @RequestBody SIOrderRequest request
    ){
        return service.order(request);
    }

    /**
     * 조회 (단건)
     */
    @PostMapping("/search")
    public SISearchResponse search(
            @RequestBody SISearchRequest request
    ){
        return service.search(request);
    }

    /**
     * 조회(다건)
     */
    @PostMapping("/search/order")
    public SIOrderSearchResponse searchByOrderNo(
            @RequestBody SIOrderSearchRequest request
    ){
        return service.searchByOrderNo(request);
    }

    /**
     * 취소 단건
     */
    @PostMapping("/cancel/single")
    public SICancelResponse cancelSingle(
            @RequestBody SICancelRequest request
    ){
        return service.cancelSingle(request);
    }

    /**
     * 취소 다건
     */
    @PostMapping("/cancel/multi")
    public SICancelListResponse cancelMulti(
            @RequestBody SICancelListRequest request
    ){
        return service.cancelMulti(request);
    }

    /**
     * 상품 조회
     */
    @PostMapping("/product")
    public List<SIProductResponse> product(
            @RequestBody SIProductRequest request
    ){
        return service.product(request);
    }

    /**
     * 문자 재전송
     */
    @PostMapping("/mms/resend")
    public SIMmsResendResponse resend(
            @RequestBody SIMmsResendRequest request
    ){
        return service.mmsResend(request);
    }
}