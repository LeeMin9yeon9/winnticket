package kr.co.winnticket.integration.aquaplanet.dto.common;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class AquaplanetUtil {

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DT17 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String yyyymmddNow() {
        return LocalDateTime.now().format(YMD);
    }

    public static String dt17Now() {
        return LocalDateTime.now().format(DT17);
    }

    public static String randomDigits(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        return sb.toString();
    }

    // Random(1) + unix time ms(13)
    public static String stdSeqNo() {
        int r = ThreadLocalRandom.current().nextInt(0, 10);
        long ms = System.currentTimeMillis();
        return r + String.valueOf(ms);
    }

    public static String localIpOrEmpty() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }
}