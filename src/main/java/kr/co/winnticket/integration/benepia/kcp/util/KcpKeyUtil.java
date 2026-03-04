package kr.co.winnticket.integration.benepia.kcp.util;

import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import java.io.FileReader;
import java.security.PrivateKey;


// KCP 개인키 로딩
public class KcpKeyUtil {
    private KcpKeyUtil(){}

    public static PrivateKey loadPrivateKey(String keyPath, String password) {

        try (PEMParser parser = new PEMParser(new FileReader(keyPath))) {

            Object object = parser.readObject();

            // 암호화된 PKCS8 개인키
            PKCS8EncryptedPrivateKeyInfo encryptedPrivateKeyInfo =
                    (PKCS8EncryptedPrivateKeyInfo) object;

            var decryptorProvider =
                    new JceOpenSSLPKCS8DecryptorProviderBuilder()
                            .build(password.toCharArray());

            var privateKeyInfo =
                    encryptedPrivateKeyInfo.decryptPrivateKeyInfo(decryptorProvider);

            return new JcaPEMKeyConverter()
                    .getPrivateKey(privateKeyInfo);

        } catch (Exception e) {
            throw new RuntimeException("KCP PrivateKey load fail", e);
        }
    }
}
