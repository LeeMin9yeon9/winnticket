package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 문자템플릿] ProductSmsTemplateDto")
public class ProductSmsTemplateDto {
    @Schema(description = "템플릿코드")
    private SmsTemplateCode templateCode; // ORDER_RECEIVED, PAYMENT_CONFIRMED...

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;
}