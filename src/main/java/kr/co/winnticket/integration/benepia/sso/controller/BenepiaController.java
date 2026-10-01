package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        String channel = request.getParameter("channel");
        String returnurl = request.getParameter("returnurl");

        log.info("BENEPIA ENTRY channel={}", channel);
        log.info("BENEPIA RETURNURL={}", returnurl);

        // encParam 있으면 베네피아 유저 처리
        // 베네피아 쪽에서 encParam이 깨진 채로(길이 손실 등) 넘어오는 경우가 있는데,
        // 여기서 예외가 그대로 터지면 고객이 에러 응답을 그대로 보게 됨 - returnurl처럼
        // 안전하게 홈으로 fallback 시키고, 복호화된 회원 정보 없이 채널만 표시한다.
        if (encParam != null && !encParam.isBlank()) {
            try {
                entryService.handle(encParam, session);
                channel = "BENE";
            } catch (Exception e) {
                log.error("[BENEPIA] encParam 복호화 실패 - 홈으로 fallback", e);
                channel = "BENE";
            }
        } else {
            channel = "DEFAULT";
        }

        session.setAttribute("CHANNEL_CODE", channel);
        log.info("BENEPIA session channel={}", channel);


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
                    decodedUrl += (decodedUrl.contains("?") ? "&" : "?") + "channel=" + channel;
                }

                log.info("FINAL REDIRECT URL = {}", decodedUrl);

                return "redirect:" + decodedUrl;
            } else {
                log.warn("INVALID returnurl = {}", decodedUrl);
            }
        }

        // fallback 유지 (기존 기능 보호)
        return "redirect:/?channel=" + channel;
    }


    @GetMapping("/session")
    @ResponseBody
    @Operation(summary = "베네피아 세션 조회", description = "프론트에서 channelCode 조회")

    public Map<String, Object> getSession(HttpSession session) {

        log.info("[BENEPIA] SESSION CHECK");

        String channelCode = (String) session.getAttribute("CHANNEL_CODE");


        if (channelCode == null) {
            return Map.of("channelCode", "DEFAULT");


        }
        // 없으면 무조건 DEFAULT
        return Map.of("channelCode", channelCode);
    }
}
