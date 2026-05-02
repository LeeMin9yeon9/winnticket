package kr.co.winnticket.integration.benepia.batch.controller;

import kr.co.winnticket.integration.benepia.batch.service.BenepiaBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/benepia")
public class BenepiaBatchController {

    private final BenepiaBatchService batchService;

    // 배치 호출 인증용 비밀 헤더값. 미설정 시 엔드포인트 비활성.
    @Value("${winn.batch.secret:}")
    private String batchSecret;

    @PostMapping("/batch/ticket/run")
    public ResponseEntity<?> runTicketBatch(
            @RequestHeader(value = "X-Batch-Secret", required = false) String secretHeader
    ) {
        if (batchSecret == null || batchSecret.isBlank()) {
            log.warn("[BATCH] winn.batch.secret 미설정 → 배치 엔드포인트 비활성");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("disabled");
        }
        if (secretHeader == null || !batchSecret.equals(secretHeader)) {
            log.warn("[BATCH] X-Batch-Secret 헤더 불일치 또는 누락");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
        try {
            batchService.executeTicketBatch();
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}