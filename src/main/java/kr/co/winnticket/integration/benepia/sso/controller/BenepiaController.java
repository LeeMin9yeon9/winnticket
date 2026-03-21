package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log4j2
@Tag(name = "베네피아", description = "베네피아 -> 윈앤티켓 웹연동(ECB)1")
@Controller
@RequiredArgsConstructor
@RequestMapping({"/benepia", "/api/benepia"})
public class BenepiaController {

    private final BenepiaEntryService entryService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "베네피아 > 윈앤티켓 복호화", description = "베네피아에서 전달된 encParam을 복호화하여 세션에 저장한 윈앤티켓으로 옴"
    )
    public String entry(HttpServletRequest request, HttpSession session) {

        String encParam = request.getParameter("encParam");
        String returnurl = request.getParameter("returnurl");

        log.info("BENEPIA RETURNURL={}", returnurl);

        // encParam 있으면 베네피아 유저 처리
        if(encParam != null && !encParam.isBlank()){
            entryService.handle(encParam, session);

            // 베네피아 채널 세팅
            session.setAttribute("CHANNEL_CODE", "BENE");
            session.setAttribute("BENEPIA_ENTRY", true);
        }else{
            log.info("[BENEPIA] 일반 접근 → 세션 초기화");

            session.removeAttribute("BENEP_DECRYPTED");
            session.removeAttribute("BENEP_TKN_KEY");
            session.removeAttribute("BENEPIA_ENTRY");

            session.setAttribute("CHANNEL_CODE", "DEFAULT");
        }

        //session.setAttribute("CHANNEL_CODE", channel);

        // returnurl 있을 때만 처리
        if (returnurl != null && !returnurl.isBlank()) {

            String decodedUrl = returnurl;

            try {
                // 인코딩된 경우만 decode
                if (returnurl.contains("%")) {
                    decodedUrl = URLDecoder.decode(returnurl, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                log.warn("RETURNURL decode 실패, raw 사용", e);
            }

            log.info("DECODED returnurl = {}", decodedUrl);

            // 내부 경로만 허용
            if (decodedUrl.startsWith("/")) {

                if (!decodedUrl.contains("channel=")) {
                    decodedUrl += (decodedUrl.contains("?") ? "&" : "?") + "channel=" +  session.getAttribute("CHANNEL_CODE");
                }

                log.info("FINAL REDIRECT URL = {}", decodedUrl);

                return "redirect:" + decodedUrl;
            } else {
                log.warn("INVALID returnurl = {}", decodedUrl);
            }
        }

        // fallback 유지 (기존 기능 보호)
        return "redirect:/shop?channel=" + session.getAttribute("CHANNEL_CODE");
    }


    @GetMapping("/session")
    @ResponseBody
    @Operation(summary = "베네피아 세션 조회", description = "프론트에서 channelCode 조회")

    public Map<String, Object> getSession(HttpSession session) {

        log.info("[BENEPIA] SESSION CHECK");

        String channelCode = (String) session.getAttribute("CHANNEL_CODE");
        Object benepiaUser = session.getAttribute("BENEP_DECRYPTED");
        Boolean isBenepia = (Boolean) session.getAttribute("BENEPIA_ENTRY");

        log.info("channelCode={}, benepiaUser={}, isBenepia={}", channelCode, benepiaUser, isBenepia);

        //  베네피아 세션 있으면 유지
        if (Boolean.TRUE.equals(isBenepia) && benepiaUser != null) {
            return Map.of("channelCode", "BENE");
        }

        // 일반 유저 → DEFAULT 강제
        log.info("DEFAULT MALL → RESET");

        log.info("[BENEPIA] DEFAULT 전환");

        session.removeAttribute("BENEP_DECRYPTED");
        session.removeAttribute("BENEP_TKN_KEY");
        session.removeAttribute("BENEPIA_ENTRY");

        session.setAttribute("CHANNEL_CODE", "DEFAULT");

        return Map.of("channelCode", "DEFAULT");
    }
}
