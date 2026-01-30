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
            String urlDecoded = URLDecoder.decode(encParam,StandardCharsets.UTF_8);

            // Base64 디코딩
            byte[] cipher = Base64.getDecoder().decode(urlDecoded);

            // SEED ECB 복호화
            byte[] key = seedKey.getBytes(StandardCharsets.ISO_8859_1);
            byte[] plain = KISA_SEED_ECB.SEED_ECB_Decrypt(key,cipher,0,cipher.length);

            log.info("[BENEPIA][DECRYPT SUCCESS !!!!!!!!!!!!!!!!!]");
            return new String(plain, StandardCharsets.UTF_8).trim();

        } catch(Exception e){
            log.error("[BENEPIA][DECRYPT FAIL !!!!!!!!!!!!!!!!!]", e);
            throw new IllegalStateException("Benepia SEED-ECB decrypt 실패", e);
        }
    }

    // 토큰 생성용 encParam 암호화
    public String encrypt(String plainText){
        try{
            byte[] key = properties.getSeedKey().getBytes(StandardCharsets.ISO_8859_1);
            byte[] plain = plainText.getBytes(StandardCharsets.UTF_8);

            byte[] cipher = KISA_SEED_ECB.SEED_ECB_Encrypt(key,plain,0,plain.length);

            log.info("[BENEPIA][ENCRYPT SUCCESS !!!!!!!!!!!!!!!!!]");
            return Base64.getEncoder().encodeToString(cipher);
        }catch (Exception e){
            log.error("[BENEPIA] ENCRYPT FAIL !!!!!!!!!!!!!!!!!", e);
            throw new IllegalStateException("Benepia encrypt 실패",e);
        }
    }
}