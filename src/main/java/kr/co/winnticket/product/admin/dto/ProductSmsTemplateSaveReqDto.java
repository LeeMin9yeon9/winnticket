package kr.co.winnticket.product.admin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductSmsTemplateSaveReqDto {
    private List<ProductSmsTemplateDto> templates;
}
