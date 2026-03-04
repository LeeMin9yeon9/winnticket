package kr.co.winnticket.integration.benepia.sso.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
public class BenepiaRootController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String root(
            @RequestParam(value = "encParam", required = false) String encParam,
            @RequestParam(value = "channel", required = false) String channel
    ) {

        log.info("ROOT HIT encParam={}, channel={}", encParam, channel);

        if (encParam != null && !encParam.isBlank()) {
            log.info("BENEPIA SSO REQUEST → forward /benepia");
            return "forward:/benepia";
        }

        log.info("NORMAL USER → redirect /shop");

        return "redirect:/shop";
    }
}
