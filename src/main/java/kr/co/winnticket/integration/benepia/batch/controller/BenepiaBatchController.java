package kr.co.winnticket.integration.benepia.batch.controller;

import kr.co.winnticket.integration.benepia.batch.service.BenepiaBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 베네피아 티켓 배치를 외부에서 트리거하는 컨트롤러.
 *
 * <h3>호출 방법</h3>
 * <pre>
 * curl -X POST \
 *   -H "X-Batch-Secret: ${BATCH_SECRET}" \
 *   https://www.winnticket.co.kr/api/benepia/batch/ticket/run
 * </pre>
 *
 * <h3>환경변수 설정</h3>
 * 운영서버 systemd unit 또는 환경변수로 {@code BATCH_SECRET} 설정 필요.
 * 미설정 시 엔드포인트가 503 으로 비활성됨.
 *
 * <pre>
 * # 운영서버: /etc/systemd/system/app.service.d/env.conf
 * [Service]
 * Environment=BATCH_SECRET=&lt;랜덤 32자 이상 문자열&gt;
 * </pre>
 *
 * <h3>응답 코드</h3>
 * <ul>
 *   <li>200 OK — 배치 정상 실행</li>
 *   <li>401 Unauthorized — 헤더 누락 / 값 불일치</li>
 *   <li>503 Service Unavailable — 서버에 BATCH_SECRET 미설정</li>
 *   <li>500 Internal Server Error — 배치 실행 중 예외</li>
 * </ul>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/benepia")
public class BenepiaBatchController {

    private final BenepiaBatchService batchService;

    /**
     * 배치 호출 인증용 비밀 헤더값.
     * 운영서버 환경변수 {@code BATCH_SECRET}에서 주입.
     * 호출 측은 동일 값을 {@code X-Batch-Secret} 헤더로 보내야 함.
     * 미설정(blank) 시 엔드포인트 503 으로 비활성.
     */
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