package kr.co.winnticket.integration.benepia.kcp.util;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

// 전자서명 생성
public class KcpSignUtil {
    private KcpSignUtil(){}

    public static String makeSignature(
            String siteCd,          // 가맹점코드
            String tno,             // 거래번호
            String modType,         // STSC 취소 / STRA 재승인
            PrivateKey privateKey   // 개인키
    ) {
        try {

            // 서명 대상 데이터 구성
            String target = siteCd + "^" + tno + "^" + modType;

            // SHA256WithRSA 서명 객체 생성
            Signature signature = Signature.getInstance("SHA256WithRSA");

            // 개인키로 서명 초기화
            signature.initSign(privateKey);

            // 대상 데이터 입력
            signature.update(target.getBytes(StandardCharsets.UTF_8));

            // 서명 실행
            byte[] signedBytes = signature.sign();

            // Base64 인코딩
            return Base64.getEncoder().encodeToString(signedBytes);

        } catch (Exception e) {
            throw new RuntimeException("KCP signature create fail", e);
        }
    }

}
