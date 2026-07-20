package kr.co.winnticket.integration.benepia.crypto;

import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class BenepiaParamParser {

    // 베네피아가 encParam 안의 값들을 EUC-KR로 percent-encoding해서 보냄 (UTF-8 아님)
    private static final Charset EUC_KR = Charset.forName("EUC-KR");

    public BenepiaDecryptedParamDto parse(String decrypted) {

        Map<String, String> map = new HashMap<>();

        for (String p : decrypted.split("&")) {
            int i = p.indexOf('=');
            if (i > 0) {
                map.put(
                        p.substring(0, i),
                        URLDecoder.decode(p.substring(i + 1), EUC_KR)
                );
            }
        }

       return BenepiaDecryptedParamDto.builder()
               .sitecode(map.get("sitecode"))
               .userid(map.get("userid"))
               .benefit_id(map.get("benefit_id"))
               .sitename(map.get("sitename"))
               .username(map.get("username"))
               .tknKey(map.get("tknKey"))
               .returnurl(map.get("returnurl"))
               .userKey(map.get("sitecode") + map.get("userid") + map.get("benefit_id"))
               .build();
    }
}

