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

import java.util.Map;

@Log4j2
@Tag(name = "베네피아", description = "베네피아 -> 윈앤티켓 웹연동(ECB)1")
@Controller
@RequiredArgsConstructor
@RequestMapping({"/benepia", "/api/benepia"})
public class BenepiaController {

    private final BenepiaEntryService entryService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "베네피아 > 윈앤티켓 진입", description = "베네피아에서 전달된 encParam을 복호화하여 세션에 저장한 윈앤티켓으로 옴"
    )
    public String entry(
            HttpServletRequest request,
            HttpSession session
    ) {

        String encParam = request.getParameter("encParam");
        String channel = request.getParameter("channel");

        log.info("BENEPIA CONTROLLER ENTRY channel={}", channel);

        if(encParam != null && !encParam.isBlank()){
            entryService.handle(encParam, session);

            // encParam 복호화 결과에 채널이 있으면 거기서도 가져오기 가능
            if(channel == null || channel.isBlank()){
                channel = "BENE";
            }
        }

        if(channel == null || channel.isBlank()){
            channel = "DEFAULT";
        }

        session.setAttribute("CHANNEL_CODE", channel);

        log.info("CHANNEL_CODE SESSION SET = {}", channel);

        return "redirect:/shop?channel=" + channel;
    }

    @GetMapping("/session")
    @ResponseBody
    @Operation(summary = "베네피아 세션 조회", description = "프론트에서 channelCode 조회")
    public Map<String, Object> getSession(HttpSession session){

        log.info("[BENEPIA] SESSION CHECK");

        String channelCode = (String) session.getAttribute("CHANNEL_CODE");

        if(channelCode == null){
            return Map.of("channelCode", "DEFAULT"
            );
        }

        return Map.of("channelCode", channelCode
        );
    }

    @PostMapping("/channel-init")
    @ResponseBody
    @Operation(summary = "채널 초기화", description = "URL의 channel 파라미터를 세션에 저장")
    public Map<String, Object> initChannel(
            @RequestParam(required = false) String channel,
            HttpSession session
    ) {

        if (channel == null || channel.isBlank()) {
            channel = "DEFAULT";
        }

        session.setAttribute("CHANNEL_CODE", channel);

        log.info("[BENEPIA] CHANNEL INIT = {}", channel);

        return Map.of(
                "channelCode", channel
        );
    }
}
