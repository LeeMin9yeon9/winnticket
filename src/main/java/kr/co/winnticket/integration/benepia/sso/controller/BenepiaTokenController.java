package kr.co.winnticket.integration.benepia.sso.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.benepia.sso.service.BenepiaTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/benepia/ssoTest")
@Tag(name = "베네피아" , description ="토큰생성 테스트용 API" )
public class BenepiaTokenController {

    private final BenepiaTokenService tokenService;
    @GetMapping("/token")
    public String generate(@RequestParam String userid) {

        String response = tokenService.createToken(userid);

        if(response == null || response.isBlank()){
            return """
            RESPONSE IS NULL OR EMPTY
            """;
        }

        return """
         RESPONSE :
        """.formatted(response);
    }


    }
