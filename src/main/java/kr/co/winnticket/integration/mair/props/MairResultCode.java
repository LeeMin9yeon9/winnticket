package kr.co.winnticket.integration.mair.props;

import java.util.Map;

public class MairResultCode {
    private static final Map<String, String> MESSAGE = Map.of(
            "OK", "성공",
            "91", "요청 값 누락",
            "92", "일치하는 채널아이디 없음/조회정보 없음",
            "93", "일치하는 상품 없음/취소된 쿠폰",
            "94", "이미 발송한 거래번호/발권된 쿠폰",
            "95", "쿠폰 핀번호 없음/상태값 오류"
    );

    public static String message(String code){
        return MESSAGE.getOrDefault(code,"알 수 없는 코드: "+ code);
    }
}
