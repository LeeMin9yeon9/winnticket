package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "베네피아", description = "연동 > 베네피아")
@Controller
@RequiredArgsConstructor
@RequestMapping("/benepia")
public class BenepiaController {

    private final BenepiaEntryService entryService;

    @GetMapping
    @Operation(summary = "베네피아 > 윈앤티켓", description = "베네피아에서 전달된 encParam을 복호화하여 사용자 식별 정보 확인한다."
    )
    public String entry(
            @RequestParam(value = "encParam", required = false) String encParam,
            @RequestParam(value = "returnurl", required = false) String returnurl,
            HttpSession session
    ) {
        if (encParam != null && !encParam.isBlank()) {
            entryService.process(encParam, returnurl, session);
            session.setAttribute("CHANNEL", "BENE");
        }

        // 🔥 핵심 한 줄
        return "forward:/index.html";
    }
}
