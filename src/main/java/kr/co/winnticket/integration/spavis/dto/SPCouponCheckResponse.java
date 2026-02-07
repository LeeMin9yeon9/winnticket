package kr.co.winnticket.integration.spavis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "data")
public class SPCouponCheckResponse {

    @JacksonXmlProperty(localName = "coupon_no")
    private String couponNo;

    @JacksonXmlProperty(localName = "cust_id")
    private String custId;

    @JacksonXmlProperty(localName = "rtn_div")
    private String rtnDiv;

    @JacksonXmlProperty(localName = "rtn_msg")
    private String rtnMsg;

    @JacksonXmlElementWrapper(localName = "rtn_coupons")
    @JacksonXmlProperty(localName = "rtn_coupon")
    private List<RtnCoupon> coupons;

    @Data
    public static class RtnCoupon {
        @JacksonXmlProperty(localName = "rtn_coupon_no")
        private String couponNo;

        @JacksonXmlProperty(localName = "rtn_status_div")
        private String statusDiv;

        @JacksonXmlProperty(localName = "rtn_result_date")
        private String resultDate;
    }
}
