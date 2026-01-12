package kr.co.winnticket.integration.benepia.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class BenepiaEncryptTest implements CommandLineRunner {

    private final BenepiaSeedEcbCrypto crypto;

    @Override
    public void run(String... args) {

        String plainText =
                "sitecode=0000" +
                        "&sitename=test" +
                        "&userid=test123" +
                        "&username=testuser" +
                        "&benefit_id=testid" +
                        "&tknKey=abcdefg12345";

        String encParam = crypto.encrypt(plainText);
        System.out.println("encParam=");
        System.out.println(encParam);

        // 바로 복호화까지
        String decrypted = crypto.decrypt(encParam);
        System.out.println("decrypted=");
        System.out.println(decrypted);
    }
}
