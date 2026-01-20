package kr.co.winnticket.integration.payletter.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PayletterHashUtil {
    public static String sha256(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
