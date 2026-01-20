package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "[Payletter 결제요청 DTO] PayMentRequestResDto")
public class PayletterPaymentReqDto {

    @NotNull
    @JsonProperty("pgcode")
    @Schema(description = "결제수단 코드",  example = "creditcard")
    private String pgCode;

    @NotNull
    @JsonProperty("user_id")
    @Schema(description = "가맹점 결제자 ID")
    private String userId;

    @JsonProperty("user_name")
    @Schema(description = "가맹점 결제자 이름")
    private String userName;

    @JsonProperty("service_name")
    @Schema(description = "결제 서비스명")
    private String serviceName;

    @NotNull
    @JsonProperty("client_id")
    @Schema(description = "가맹점 ID")
    private String clientId;

    @JsonProperty("order_no")
    @Schema(description = "가맹점 주문번호")
    private String orderNo;

    @NotNull
    @Schema(description = "결제금액")
    private Integer amount;

    @JsonProperty("taxfree_amount")
    @Schema(description = "비과세 금액")
    private Integer taxfreeAmount;

    @JsonProperty("tax_amount")
    @Schema(description = "부가세 금액")
    private Integer taxAmount;

    @NotNull
    @JsonProperty("product_name")
    @Schema(description = "결제상품")
    private String productName;

    @JsonProperty("email_flag")
    @Schema(description = "결제내역 메일 수신 여부", example = "Y / N")
    private String emailFlag;

    @JsonProperty("email_addr")
    @Schema(description = "결제내역 메일 주소")
    private String emailAddr;

    @JsonProperty("autopay_flag")
    @Schema(description = "자동결제 여부" , example = "Y / N")
    private String autopayFlag;

    @JsonProperty("custom_parameter")
    @Schema(description = "가맹점이 전송하는 임의의 값 (필요 정보 셋팅하면 결제 결과로 리턴)")
    private String customParameter;

    @NotNull
    @JsonProperty("return_url")
    @Schema(description = "결제 완료 후 연결할 웹 페이지 URL")
    private String returnUrl;

    @NotNull
    @JsonProperty("callback_url")
    @Schema(description = "결제 성공 결과를 수신할 URL")
    private String callbackUrl;


    @JsonProperty("cancel_url")
    @Schema(description = "결제 취소 URL")
    private String cancelUrl;












}
