package kr.co.winnticket.integration.benepia.sso.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// "베네피아 제휴몰 진입 흐름 service",  "encParam 복호화 -> 사용자정보 추출 -> Benepiacontext 생성")
public class BenepiaEntryService {

    private final BenepiaDecryptService decryptService;
    private final BenepiaContextService contextService;

    public void process(String encParam, String returnurl, HttpSession session) {

        BenepiaDecryptedParamDto dto = decryptService.decrypt(encParam);
        dto.setReturnurl(returnurl);

        // SSO 핵심 정보 세션 저장
        session.setAttribute("BENEPIA_USER", dto);
    }
//    public BenepiaSsoResDto entry(String encParam, String returnurl, HttpSession session) {
//
//        BenepiaDecryptedParamDto decrypted = decryptService.decrypt(encParam);
//        contextService.store(session, decrypted);
//
//        return BenepiaSsoResDto.builder()
//                .success(true)
//                .user(
//                        BenepiaSsoUserDto.builder()
//                                .userId(decrypted.getUserid())
//                                .userName(decrypted.getUsername())
//                                .build()
//                )
//                .build();
    }
