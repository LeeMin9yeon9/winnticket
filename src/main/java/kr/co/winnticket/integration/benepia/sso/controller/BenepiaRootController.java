package kr.co.winnticket.integration.benepia.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BenepiaRootController {
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String root(
            @RequestParam(value = "encParam", required = false) String encParam
            //@RequestParam(value = "channel", required = false) String channel
    ) {

        if (encParam != null) {

            return "forward:/benepia";
        }

        return "redirect:/shop";
    }
}
