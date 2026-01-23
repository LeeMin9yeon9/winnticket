package kr.co.winnticket.integration.payletter.config;

public class PayletterMapper {
    public static String toPayletterPgCode(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("paymentMethod is empty");
        }

        return switch (paymentMethod.toUpperCase()) {
            // 신용카드
            case "CARD" -> "creditcard";

            // 계좌이체
            case  "BANK_TRANSFER" -> "BANK";

            // 휴대폰결제
            case "KAKAOPAY" -> "kakaopay";

            default -> "creditcard";
        };
    }
}
