package kr.co.winnticket.integration.benepia.crypto;

import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BenepiaParamParser {

    public BenepiaDecryptedParamDto parse(String decrypted) {

        Map<String, String> map = new HashMap<>();

        for (String pair : decrypted.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }

        BenepiaDecryptedParamDto dto = new BenepiaDecryptedParamDto();
        dto.setSitecode(map.get("sitecode"));
        dto.setSitename(map.get("sitename"));
        dto.setUserid(map.get("userid"));
        dto.setUsername(map.get("username"));
        dto.setBenefit_id(map.get("benefit_id"));
        dto.setTknKey(map.get("tknKey"));

        return dto;
    }
}

