package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/benepia/token")
@Tag(name = "베네피아" , description ="베네피아 SSO 토큰" )
public class BenepiaTokenController {

    private final BenepiaTokenService tokenService;
    @RequestMapping(value = "/create-confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public String createConfirm(HttpSession session){

        log.info("[BENEPIA][TOKEN][CTRL HIT]");

        BenepiaDecryptedParamDto decryptedParamDto = (BenepiaDecryptedParamDto) session.getAttribute("BENEP_DECRYPTED");

        if(decryptedParamDto == null){
            throw new IllegalStateException("베네피아 웹 진입 후 호출해야 함");
        }

        String tknKey = tokenService.createToken(decryptedParamDto);
        tokenService.confirmToken(tknKey);

        session.setAttribute("BENEP_SSO_TKE_KEY",tknKey);
        return "SSO TOKEN OK";
    }


    }
