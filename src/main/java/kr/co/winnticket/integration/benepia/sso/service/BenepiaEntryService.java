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

            try {
                // 1. 복호화
                String decrypted = crypto.decrypt(encParam);

                // 2. 파싱
                BenepiaDecryptedParamDto dto = parser.parse(decrypted);
                dto.setReturnurl(returnurl);

                // 3. SSO Confirm
                BenepiaSsoResDto confirmRes = sso.confirm(dto.getTknKey());

                if (confirmRes == null) {
                    // 로그만 남기고 종료
                    log.warn("Benepia SSO confirm response is null");
                    return;
                }

                if (!"S000".equals(confirmRes.getResponseCode())) {
                    log.warn("Benepia SSO failed: {} / {}",
                            confirmRes.getResponseCode(),
                            confirmRes.getResponseMessage());
                    return;
                }

                // 4. 세션 저장 (세션 있을 때만)
                if (session != null) {
                    session.setAttribute("BENE_USER", dto);
                    session.setAttribute("CHANNEL", "BENE");
                }

            } catch (Exception e) {
                // 절대 throw 하지 말 것
                log.error("Benepia SSO process error", e);
            }
    }
