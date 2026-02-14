package kr.co.winnticket.integration.aquaplanet.dto.common;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class AquaplanetUtil {

    private static final DateTimeFormatter D8 = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DT17 = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String yyyymmddNow() {
        return LocalDate.now().format(D8);
    }

    public static String dt17Now() {
        return LocalDateTime.now().format(DT17);
    }

    // 문서: Random(5)
    public static String randomDigits(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        return sb.toString();
    }

    // 문서: Random(1) + unix time(13)
    public static String stdSeqNo() {
        long ms = System.currentTimeMillis(); // 13
        return randomDigits(1) + ms;
    }

    public static String localIpOrEmpty() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }
}
