package kr.co.winnticket.integration.spavis.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import kr.co.winnticket.integration.spavis.props.SpavisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SpavisClient {

    private final RestTemplate spavisRestTemplate;
    private final SpavisProperties props;
    private final XmlMapper xmlMapper = new XmlMapper();

    public SPCouponCheckResponse checkCoupon(String couponNo) throws Exception {

        String url = props.getBaseUrl()
                + "?coupon_no=" + couponNo
                + "&cust_id=" + props.getCustId();

        String xml = spavisRestTemplate.getForObject(url, String.class);

        return xmlMapper.readValue(xml, SPCouponCheckResponse.class);
    }
}
