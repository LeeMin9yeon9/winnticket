package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PayletterTransactionItemDto {

    @NotNull
    @JsonProperty("pgcode")
    @Schema(description = "결제수단 코드",  example = "creditcard")
    private String pgCode;

    @JsonProperty("user_id")
    @Schema(description = "가맹점 결제자 ID")
    private String userId;

    @JsonProperty("user_name")
    @Schema(description = "가맹점 결제자 이름")
    private String userName;

    @JsonProperty("tid")
    @Schema(description = "결제 고유 번호")
    private String tid;

    @JsonProperty("승인번호")
    private String cid;

    @Schema(description = "취소 금액")
    @JsonProperty("amount")
    private Integer amount;

    @Schema(description = "가맹점 주문번호")
    @JsonProperty("order_no")
    private String orderNo;

    @Schema(description = "상품명")
    @JsonProperty("product_name")
    private String productName;

    @Schema(description = "상태(0:승인 , 1:전체취소, 2:부분취소")
    @JsonProperty("status_code")
    private Integer statusCode;

    @Schema(description = "결제일시")
    @JsonProperty("transaction_date")
    private String transactionDate;

    @Schema(description = "취소 일시")
    @JsonProperty("cancel_date")
    private String cancelDate;


}
