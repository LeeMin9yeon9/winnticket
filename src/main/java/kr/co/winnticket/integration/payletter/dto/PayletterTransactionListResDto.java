package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[Payletter 결제내역 조회 응답 DTO] PayletterTransactionListResDto")
public class PayletterTransactionListResDto {

    @Schema(description = "에러코드")
    private Integer code;

    @Schema(description = "에러 메세지")
    private String message;

    @Schema(description = "전체 조회건수")
    @JsonProperty("total_count")
    private Integer totalCount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item{

        @Schema(description = "사용 결제수단 코드", example = "creditcard")
        @JsonProperty("pgcode")
        private String pgCode;

        @JsonProperty("user_id")
        @Schema(description = "결제자 ID")
        private String userId;

        @JsonProperty("user_name")
        private String userName;

        @Schema(description = "결제 고유번호")
        private String tid;

        @Schema(description = "승인 번호")
        private String cid;

        @Schema(description = "결제금액(취소금액)")
        private Integer amount;

        @Schema(description = "비과세 금액")
        @JsonProperty("taxfree_amount")
        private Integer taxfreeAmount;

        @Schema(description = "부가세 금액")
        @JsonProperty("tax_amount")
        private Integer taxAmount;

        @Schema(description = "가맹점 주문번호")
        @JsonProperty("order_no")
        private String orderNo;

        @Schema(description = "상품명")
        @JsonProperty("product_name")
        private String productName;

        @Schema(description = "상태 (0:승인 , 1:전체취소, 2:부분 취소")
        @JsonProperty("status_code")
        private Integer statusCode;

        @Schema(description = "결제일시")
        @JsonProperty("transaction_date")
        private String transactionDate;

        @Schema(description = "취소일시")
        @JsonProperty("cancel_date")
        private String cancelDate;
    }

    public boolean isSuccess() {
        return code != null && code == 0;
    }
}
