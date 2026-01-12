package kr.co.winnticket.integration.benepia.sso.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.crypto.BenepiaParamParser;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaSsoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
// "베네피아 제휴몰 진입 흐름 service",  "encParam 복호화 -> 사용자정보 추출 -> Benepiacontext 생성")
public class BenepiaEntryService {

    private final BenepiaSeedEcbCrypto crypto;
    private final BenepiaParamParser parser;
    private final BenepiaSsoService sso;

    public void process(String encParam, String returnurl, HttpSession session) {

        if (encParam == null || encParam.isBlank()) {
            log.warn("[BENEPia] encParam is null or blank");
            return;
        }

        try {
            log.info("[BENEPia] encParam raw = {}", encParam);

            // 1. 복호화
            String decrypted = crypto.decrypt(encParam);
            log.info("[BENEPia] decrypted = {}", decrypted);

            // 2. 파싱
            BenepiaDecryptedParamDto dto = parser.parse(decrypted);
            dto.setReturnurl(returnurl);
            log.info("[BENEPia] parsed dto = {}", dto);

            // 3. SSO Confirm
            BenepiaSsoResDto confirmRes = sso.confirm(dto.getTknKey());

            if (confirmRes == null) {
                log.warn("[BENEPia] SSO confirm response is null");
                return;
            }

            if (!"S000".equals(confirmRes.getResponseCode())) {
                log.warn("[BENEPia] SSO failed: {} / {}",
                        confirmRes.getResponseCode(),
                        confirmRes.getResponseMessage());
                return;
            }

            // 4. 세션 저장
            if (session != null) {
                session.setAttribute("BENE_USER", dto);
                session.setAttribute("CHANNEL", "BENE");
            }

            log.info("[BENEPia] SSO success. Session stored.");

        } catch (Exception e) {
            log.error("[BENEPia] SSO process error", e);
        }
    }
}