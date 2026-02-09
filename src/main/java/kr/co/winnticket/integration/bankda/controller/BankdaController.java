package kr.co.winnticket.integration.bankda.controller;

import kr.co.winnticket.integration.bankda.dto.BankdaTransaction;
import kr.co.winnticket.integration.bankda.service.BankdaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bankda/test")
public class BankdaController {

    private final BankdaService service;

    @PostMapping("/today")
    public List<BankdaTransaction> today() {
        return service.fetchToday();
    }
}
