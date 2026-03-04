package kr.co.winnticket.integration.benepia.sso.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BenepiaRootController {

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String root(HttpServletRequest request) {

        String encParam = request.getParameter("encParam");

        if (encParam != null && !encParam.isBlank()) {

            return "forward:/benepia";
        }

        return "redirect:/shop";
    }
}
