package kr.co.winnticket.integration.benepia.batch.controller;

import kr.co.winnticket.integration.benepia.batch.service.BenepiaBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/benepia")
public class BenepiaBatchController {

    private final BenepiaBatchService batchService;

    @PostMapping("/batch/ticket/run")
    public ResponseEntity<?> runTicketBatch() {
        try {
            batchService.executeTicketBatch();
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}