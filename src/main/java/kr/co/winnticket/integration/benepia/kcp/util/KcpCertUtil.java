package kr.co.winnticket.integration.benepia.kcp.util;

import java.nio.file.Files;
import java.nio.file.Paths;


// 인증서 전용
public class KcpCertUtil {
    public static String loadCert(String certPath){
        try {
            String cert = Files.readString(Paths.get(certPath));

            // 모든 개행 제거
            cert = cert.replace("\r", "")
                    .replace("\n", "");

            return cert.trim();

        } catch (Exception e){
            throw new RuntimeException("KCP cert load fail",e);
        }
    }
}
