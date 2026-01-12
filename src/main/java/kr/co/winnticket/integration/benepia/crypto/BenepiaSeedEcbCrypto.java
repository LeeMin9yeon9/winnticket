package kr.co.winnticket.integration.benepia.crypto;

import jakarta.annotation.PostConstruct;
import kr.co.winnticket.integration.benepia.common.BenepiaErrorCode;
import kr.co.winnticket.integration.benepia.common.BenepiaException;
import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class BenepiaSeedEcbCrypto {

    private final BenepiaProperties properties;

    @PostConstruct
    public void validateSeedKey() {
        System.out.println(">>> BENEP SEED KEY = [" + properties.getSeedKey() + "]");
    }


     //decrypt (운영용 복호화)
    public String decrypt(String encParam) {
        try {
            // 1. URL Decode (중복 방지)
            String decoded = encParam.contains("%")
                    ? URLDecoder.decode(encParam, StandardCharsets.UTF_8)
                    : encParam;

            // 2. Base64 Decode
            byte[] encrypted = Base64.getDecoder().decode(decoded);

            // 3. SEED ECB Decrypt
            byte[] decrypted = KISA_SEED_ECB.SEED_ECB_Decrypt(
                    properties.getSeedKey().getBytes(StandardCharsets.UTF_8),
                    encrypted,
                    0,
                    encrypted.length
            );

            return new String(decrypted, StandardCharsets.UTF_8).trim();

        } catch (Exception e) {
            throw new BenepiaException(
                    BenepiaErrorCode.BENEPIA_DECRYPT_FAIL,
                    "Benepia SEED 복호화 실패",
                    e
            );
        }
    }


     //encrypt (테스트용 암호화)

    public String encrypt(String plainText) {
        try {
            // 1. 평문 → byte[]
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

            // 2. SEED ECB Encrypt
            byte[] encrypted = KISA_SEED_ECB.SEED_ECB_Encrypt(
                    properties.getSeedKey().getBytes(StandardCharsets.UTF_8),
                    plainBytes,
                    0,
                    plainBytes.length
            );

            // 3. Base64 Encode
            String base64Encoded = Base64.getEncoder().encodeToString(encrypted);

            // 4. URL Encode
            return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new BenepiaException(
                    BenepiaErrorCode.BENEPIA_DECRYPT_FAIL,
                    "Benepia SEED 암호화 실패",
                    e
            );
        }
    }
}