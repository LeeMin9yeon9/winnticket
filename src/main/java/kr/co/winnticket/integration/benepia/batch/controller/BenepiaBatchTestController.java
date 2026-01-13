package kr.co.winnticket.integration.benepia.batch.controller;

import kr.co.winnticket.integration.benepia.batch.service.BenepiaBatchUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/benepia/test")
public class BenepiaBatchTestController {

    private final BenepiaBatchUploadService service;

    @PostMapping("/run")
    public String run() throws Exception {
        service.executeBatch();
        return "BENEPia Batch SUCCESS";
    }
}

