package kr.co.winnticket.integration.benepia.crypto;

import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BenepiaParamParser {

    public BenepiaDecryptedParamDto parse(String decrypted) {

        Map<String, String> map = new HashMap<>();
        String[] pairs = decrypted.split("[&^]");

        for (String pair : pairs) {
            if (!pair.contains("=")) continue;
            String[] kv = pair.split("=", 2);
            map.put(kv[0], kv[1]);
        }

        return BenepiaDecryptedParamDto.builder()
                .sitecode(map.get("sitecode"))
                .sitename(map.get("sitename"))
                .userid(map.get("userid"))
                .username(map.get("username"))
                .benefit_id(map.get("benefit_id"))
                .tknKey(map.get("tknKey"))
                .build();
    }
}
