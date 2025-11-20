package kr.co.winnticket.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "공통", description = "공통 관리")
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping("/check")
    @Operation(summary = "상태 조회", description = "api호출 상태를 조회합니다.")
    public ResponseEntity<Void> statusCheck() {
        return ResponseEntity.ok().build(); // 200 OK
    }
}
