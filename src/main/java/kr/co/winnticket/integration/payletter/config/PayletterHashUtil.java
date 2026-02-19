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

    public static String makePayhash(String userId, String tid,Integer amount, String apiKey) {
        if (userId == null || tid == null || amount == null || apiKey == null) {

            throw new IllegalArgumentException("payhash param missing");
        }
        String raw = userId + amount + tid + apiKey;

        return sha256(raw);
    }


    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
