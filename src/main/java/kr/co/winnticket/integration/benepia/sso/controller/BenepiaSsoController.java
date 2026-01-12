package kr.co.winnticket.integration.benepia.sso.controller;

import kr.co.winnticket.integration.benepia.sso.service.BenepiaSsoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/benepia/ssoTest")
public class BenepiaSsoController {

    private final BenepiaSsoService ssoService;

    @GetMapping("/confirm")
    public String confirm(@RequestParam String tknKey){
        return ssoService.confirm(tknKey)
                ? "SSO CONFIRM SUCCESS (S000)"
                : "SSO CONFIRM FAIL";
    }
}
