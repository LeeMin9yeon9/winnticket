package kr.co.winnticket.integration.mair.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MairCouponIssueReqDto {

    private String mkno; // 채널 구분코드 : HDGH
    private String mkid; // 채널 아이디 : HDGH1
    private String itcd; // 상품 코드 : AAA0000013
    private String trno; // 주문번호(거래고유번호)
    private String odnm; // 주문자명
    private String odhp; // 발송 휴대폰번호
}
