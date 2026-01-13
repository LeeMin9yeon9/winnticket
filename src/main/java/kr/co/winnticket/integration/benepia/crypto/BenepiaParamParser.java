package kr.co.winnticket.integration.benepia.crypto;

import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class BenepiaParamParser {

    public BenepiaDecryptedParamDto parse(String decrypted) {

        Map<String, String> map = new HashMap<>();

        for (String p : decrypted.split("&")) {
            int i = p.indexOf('=');
            if (i > 0) {
                map.put(
                        p.substring(0, i),
                        URLDecoder.decode(p.substring(i + 1), StandardCharsets.UTF_8)
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

