package kr.co.winnticket.integration.benepia.crypto;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Tag(name = "베네피아", description = "SEED(ECB Crypto)")
public class BenepiaSeedEcbCrypto {

    public String decrypt(String encParamBase64,String seedKey){
        try{
            byte[] cipher = Base64.getDecoder().decode(encParamBase64);
            byte[] key = seedKey.getBytes(StandardCharsets.ISO_8859_1);

            byte[] plain = KISA_SEED_ECB.SEED_ECB_Decrypt(key,cipher,0,cipher.length);

            return new String(plain, StandardCharsets.UTF_8).trim();
        } catch(Exception e){
            throw new IllegalStateException("Benepia SEED-ECB decrypt 실패", e);
        }
    }
}