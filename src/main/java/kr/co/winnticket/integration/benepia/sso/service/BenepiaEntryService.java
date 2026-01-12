package kr.co.winnticket.integration.benepia.sso.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.crypto.BenepiaParamParser;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// "베네피아 제휴몰 진입 흐름 service",  "encParam 복호화 -> 사용자정보 추출 -> Benepiacontext 생성")
public class BenepiaEntryService {

    private final BenepiaSeedEcbCrypto crypto;
    private final BenepiaParamParser parser;
    private final BenepiaSsoService sso;

        public void process(String encParam, String returnurl, HttpSession session) {

            // 1. 복호화
            String decrypted = crypto.decrypt(encParam);

            // 2. 파싱
            BenepiaDecryptedParamDto dto = parser.parse(decrypted);
            dto.setReturnurl(returnurl);

            // 3. SSO Confirm
            if (!sso.confirm(dto.getTknKey())) {
                throw new IllegalStateException("베네피아 SSO 토큰 검증 실패");
            }

            // 4. 세션 저장
            session.setAttribute("BENE_USER", dto);
            session.setAttribute("CHANNEL", "BENE");
        }
    }
