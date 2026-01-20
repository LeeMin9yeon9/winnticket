package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(title = "[Payletter 결제요청 DTO] PayMentRequestResDto")
public class PayletterPaymentReqDto {

    @NotNull
    @JsonProperty("pgcode")
    @Schema(description = "결제수단 코드",  example = "creditcard")
    private String pgCode;

    @NotNull
    @Schema(description = "가맹점 결제자 ID")
    private String userId;


    @Schema(description = "가맹점 결제자 이름")
    private String userName;


    @Schema(description = "결제 서비스명")
    private String serviceName;

    @NotNull

    @Schema(description = "가맹점 ID")
    private String clientId;


    @Schema(description = "가맹점 주문번호")
    private String orderNo;

    @NotNull
    @JsonProperty("amount")
    @Schema(description = "결제금액")
    private Integer amount;


    @Schema(description = "비과세 금액")
    private Integer taxfreeAmount;


    @Schema(description = "부가세 금액")
    private Integer taxAmount;

    @NotNull
    @Schema(description = "결제상품")
    private String productName;


    @Schema(description = "결제내역 메일 수신 여부", example = "Y / N")
    private String emailFlag;


    @Schema(description = "결제내역 메일 주소")
    private String emailAddr;


    @Schema(description = "자동결제 여부" , example = "Y / N")
    private String autopayFlag;


    @Schema(description = "가맹점이 전송하는 임의의 값 (필요 정보 셋팅하면 결제 결과로 리턴)")
    private String customParameter;

    @NotNull
    @Schema(description = "결제 완료 후 연결할 웹 페이지 URL")
    private String returnUrl;

    @NotNull
    @Schema(description = "결제 성공 결과를 수신할 URL")
    private String callbackUrl;


    @Schema(description = "결제 취소 URL")
    private String cancelUrl;












}
