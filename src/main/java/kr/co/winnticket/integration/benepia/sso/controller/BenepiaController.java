package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Log4j2
@Tag(name = "베네피아", description = "연동 > 베네피아")
@Controller
@RequiredArgsConstructor
@RequestMapping("/benepia")
public class BenepiaController {

    private final BenepiaEntryService entryService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "베네피아 > 윈앤티켓", description = "베네피아에서 전달된 encParam을 복호화하여 사용자 식별 정보 확인한다."
    )
    public String entry(
            @RequestParam(value = "encParam", required = false) String encParam,
            HttpSession session
    ) {
        log.info("[BENEPia] entry called. method={}, encParam exists={}",
                RequestContextHolder.currentRequestAttributes()
                        .getAttribute("org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST",
                                RequestAttributes.SCOPE_REQUEST) instanceof HttpServletRequest req
                        ? req.getMethod()
                        : "UNKNOWN",
                encParam != null
        );

        session.setAttribute("CHANNEL", "BENE");

        return "redirect:/shop?channel=BENE";
    }
}
