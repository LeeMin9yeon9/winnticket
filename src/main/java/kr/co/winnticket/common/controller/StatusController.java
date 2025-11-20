package kr.co.winnticket.status;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping("/check")
    public ResponseEntity<Void> statusCheck() {
        return ResponseEntity.ok().build(); // 200 OK
    }
}
