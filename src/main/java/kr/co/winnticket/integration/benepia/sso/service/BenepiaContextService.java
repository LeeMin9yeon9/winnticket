package kr.co.winnticket.integration.benepia.sso.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import org.springframework.stereotype.Service;

@Service
// "베네피아 사용자정보 관리 service",  "주문/결제 흐름 식별을위한 context")
public class BenepiaContextService {
    public void store(HttpSession session, BenepiaDecryptedParamDto dto) {
        session.setAttribute("BENEP_USER", dto);
    }
}
