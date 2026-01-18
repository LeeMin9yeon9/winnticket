package kr.co.winnticket.integration.payletter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payletter")
public class PayletterCallbackController {
    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody(required = false) String rawBody) {
        log.info("[PAYLETTER CALLBACK] rawBody={}", rawBody);
        return ResponseEntity.ok("OK");
    }
}
