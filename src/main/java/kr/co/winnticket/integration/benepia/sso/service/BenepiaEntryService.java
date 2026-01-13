package kr.co.winnticket.integration.benepia.sso.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import kr.co.winnticket.integration.benepia.crypto.BenepiaParamParser;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
// "베네피아 제휴몰 진입 흐름 service",  "encParam 복호화 -> 사용자정보 추출 -> Benepiacontext 생성")
public class BenepiaEntryService {

    private final BenepiaProperties properties;
    private final BenepiaSeedEcbCrypto crypto;
    private final BenepiaParamParser parser;

    public BenepiaDecryptedParamDto handle(String encParam, HttpSession session){
        try {
            // 복호화
            String decrypted = crypto.decrypt(encParam, properties.getSeedKey());
            log.info("[베네피아 복호화 성공] decrypted={}", decrypted);

            // 파싱
            BenepiaDecryptedParamDto dto = parser.parse(decrypted);

            // redirect 대비 세션 저장
            session.setAttribute("BENEP_DECRYPTED", dto);
            session.setAttribute("BENEP_TKN_KEY", dto.getTknKey());

            return dto;
        }catch (Exception e){
            log.error("[베네피아 복호화 실패] encParam={}",encParam,e);
            throw e;
        }
    }
}