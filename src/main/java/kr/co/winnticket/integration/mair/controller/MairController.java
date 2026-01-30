package kr.co.winnticket.integration.mair.controller;

import kr.co.winnticket.integration.mair.service.MairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mair")
public class MairController {

    private final MairService service;

    // 발송 테스트
    @PostMapping("/issue/{orderNumber}")
    public ResponseEntity<String> issuePost(@PathVariable String orderNumber) {
        service.issueTickets(orderNumber);
        return ResponseEntity.ok("OK");
    }

    // 취소 테스트
    @PostMapping("/cancel/{orderNumber}/{orderItemId}")
    public ResponseEntity<String> cancelPost(
            @PathVariable String orderNumber,
            @PathVariable UUID orderItemId
    ) {
        service.cancelByOrder(orderNumber, orderItemId);
        return ResponseEntity.ok("OK");
    }

    // 사용여부테스트
    @GetMapping("/use-check/{orderNumber}")
    public ResponseEntity<String> useCheck(@PathVariable String orderNumber) {
        service.useCheckByOrderNumber(orderNumber);
        return ResponseEntity.ok("OK");
    }
}
