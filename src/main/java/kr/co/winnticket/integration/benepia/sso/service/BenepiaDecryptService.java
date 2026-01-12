package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.crypto.BenepiaParamParser;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
// "베네피아 encParam 복호화 전용", "encParam -> 평문 -> BenepiaDecrytedParamDto")
public class BenepiaDecryptService {
    private final BenepiaSeedEcbCrypto crypto;
    private final BenepiaParamParser parser;

    public BenepiaDecryptedParamDto decrypt(String encParam) {
        String decrypted = crypto.decrypt(encParam);
        return parser.parse(decrypted);
    }
}
