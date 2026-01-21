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
@Schema(title = "[Payletter 결제내역 조회 요청 DTO] PayletterTransactionListReqDto")
public class PayletterTransactionListReqDto {

    @NotNull
    @Schema(description = "가맹점 ID", example = "pay_test")
    @JsonProperty("client_id")
    private String clientId;

    @NotNull
    @Schema(description = "조회일자", example = "YYYYMMDD")
    private String date;

    @NotNull
    @Schema(description = "거래상태 조회 기준(transaction/settle)", example = "transaction")
    @JsonProperty("date_type")
    private String dateType;

    @Schema(description = "사용 결제수단 코드(pgcode)", example = "creditcard")
    @JsonProperty("pdcode")
    private String pgCode;

    @Schema(description = "주문번호(order_no)")
    @JsonProperty("order_no")
    private String orderNo;



}
