package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BenepiaTokenEncParamBuilder {

    private final BenepiaSeedEcbCrypto crypto;

    public String build(String benefit_id , String userid){
        String plain = "loginId=" + benefit_id +
                "&empNo=" + userid;

        return crypto.encrypt(plain);
    }
}
