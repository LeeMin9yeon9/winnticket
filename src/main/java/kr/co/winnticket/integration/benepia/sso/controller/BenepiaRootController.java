package kr.co.winnticket.integration.benepia.sso.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Log4j2
public class BenepiaRootController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String root(HttpServletRequest request) {

        String encParam = request.getParameter("encParam");
        String channel = request.getParameter("channel");

        log.info("ROOT HIT encParam={}, channel={}", encParam, channel);

        if (encParam != null && !encParam.isBlank()) {
            log.info("BENEPIA SSO REQUEST → forward /benepia");
            return "forward:/benepia";
        }

        return "redirect:/shop";
    }
}