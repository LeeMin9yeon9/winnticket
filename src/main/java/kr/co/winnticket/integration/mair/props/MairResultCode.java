package kr.co.winnticket.integration.mair.props;

import java.util.Map;

public class MairResultCode {

    private static final Map<String, String> MESSAGE = Map.of(
            "OK", "미사용",
            "91", "요청 값 누락",
            "92", "조회정보 없음",
            "93", "취소된 쿠폰",
            "94", "발권된 쿠폰",
            "95", "상태값 오류"
    );

    public static String message(String code) {
        return MESSAGE.getOrDefault(code, "알 수 없는 코드: " + code);
    }

    // 상태 판단 메서드 추가
    public static boolean isUnused(String code) {
        return "OK".equals(code);
    }

    public static boolean isCanceled(String code) {
        return "93".equals(code);
    }

    public static boolean isIssued(String code) {
        return "94".equals(code);
    }

    public static boolean isError(String code) {
        return "91".equals(code) || "92".equals(code) || "95".equals(code);
    }

    // 사용 여부 판단
    public static boolean isUsed(String code) {
        return !isUnused(code)
                && !isCanceled(code)
                && !isIssued(code)
                && !isError(code);
    }
}