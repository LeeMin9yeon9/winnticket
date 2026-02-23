package kr.co.winnticket.integration.spavis.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "data")
public class SPCouponCheckResponse {

    @JacksonXmlProperty(localName = "coupon_no")
    private String couponNo;  // 쿠폰번호

    @JacksonXmlProperty(localName = "cust_id")
    private String custId; // 고객ID

    @JacksonXmlProperty(localName = "rtn_div")
    private String rtnDiv; // 성공여부

    @JacksonXmlProperty(localName = "rtn_msg")
    private String rtnMsg;  // 결과메세지

    @JacksonXmlElementWrapper(localName = "rtn_coupons")
    @JacksonXmlProperty(localName = "rtn_coupon")
    private List<RtnCoupon> coupons;

    @Data
    public static class RtnCoupon { // 이용결과확인
        @JacksonXmlProperty(localName = "rtn_coupon_no")
        private String couponNo; // 쿠폰번호

        @JacksonXmlProperty(localName = "rtn_status_div")
        private String statusDiv; // 상태값

        @JacksonXmlProperty(localName = "rtn_result_date")
        private String resultDate; // 사용일시
    }
}
