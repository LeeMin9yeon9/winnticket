package kr.co.winnticket.integration.benepia.crypto;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Log4j2
@Component
@RequiredArgsConstructor
@Tag(name = "베네피아", description = "SEED(ECB Crypto)")
public class BenepiaSeedEcbCrypto {

    private final BenepiaProperties properties;

    // 베네피아 -> 윈앤티켓 웹 encParam 복호화
    public String decrypt(String encParam,String seedKey){
        try{

            //String urlDecoded = URLDecoder.decode(encParam,StandardCharsets.UTF_8);
            // space → + 보정
            // URL 인코딩이 들어온 경우만 decode
            String value = encParam;
            if (value.contains("%")) {
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
            }

            // 모바일 브라우저 대응 (+ → space 되는 문제)
            value = value.replace(" ", "+");

            // Base64 디코딩
            byte[] cipher = Base64.getDecoder().decode(value);
            log.info("[BENEPIA] cipher byte length={}, %16={}, seedKey byte length={}",
                    cipher.length, cipher.length % 16, seedKey == null ? -1 : seedKey.getBytes(StandardCharsets.ISO_8859_1).length);

            // SEED ECB 복호화
            byte[] key = seedKey.getBytes(StandardCharsets.ISO_8859_1);
            byte[] plain = KISA_SEED_ECB.SEED_ECB_Decrypt(key,cipher,0,cipher.length);

            // 암호문 길이가 16바이트(SEED 블록 크기)의 배수가 아니면 SEED_ECB_Decrypt가
            // 조용히 null을 반환한다(예외 아님) - 원인을 바로 알 수 있도록 명시적으로 처리.
            if (plain == null) {
                throw new IllegalStateException(
                        "SEED 복호화 실패 - 암호문 길이가 16의 배수가 아님 (cipher.length=" + cipher.length
                                + ", %16=" + (cipher.length % 16) + ")"
                );
            }

            log.info("[BENEPIA][DECRYPT SUCCESS ]");
            String result = new String(plain, StandardCharsets.UTF_8);
            log.info("[BENEPIA] 복호화 결과 = {}", result);
            return result;

        } catch(Exception e){
            log.error("[BENEPIA][DECRYPT FAIL]", e);
            throw new IllegalStateException("Benepia SEED-ECB decrypt 실패", e);
        }
    }

    // 토큰 생성용 encParam 암호화
    public String encrypt(String plainText){
        try{
            byte[] key = properties.getSeedKey().getBytes(StandardCharsets.ISO_8859_1);
            byte[] plain = plainText.getBytes(StandardCharsets.UTF_8);

            byte[] cipher = KISA_SEED_ECB.SEED_ECB_Encrypt(key,plain,0,plain.length);

            log.info("[BENEPIA]ENCRYPT SUCCESS");
            return Base64.getEncoder().encodeToString(cipher);
        }catch (Exception e){
            log.error("[BENEPIA] ENCRYPT FAIL ", e);
            throw new IllegalStateException("Benepia encrypt 실패",e);
        }
    }
}