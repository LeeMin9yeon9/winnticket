package kr.co.winnticket.integration.mair.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "[Mair 쿠폰발송 요청 DTO] MairCouponIssueReqDto")
public class MairCouponIssueReqDto {

    @Schema(description = "채널 구분코드 : HDGH")
    @JsonProperty("MKNO")
    private String mkno; // 채널 구분코드 : HDGH

    @Schema(description = "채널 아이디 HDGH1 ")
    @JsonProperty("MKID")
    private String mkid;

    @JsonProperty("ITCD")
    @Schema(description = "상품 코드")
    private String itcd; // 상품 코드 : AAA0000013

    @JsonProperty("TRNO")
    @Schema(description = "주문번호(거래고유번호)")
    private String trno;

    @JsonProperty("ODNM")
    @Schema(description = "주문자 이름")
    private String odnm;

    @JsonProperty("ODHP")
    @Schema(description = "발송 휴대폰번호")
    private String odhp;
}
