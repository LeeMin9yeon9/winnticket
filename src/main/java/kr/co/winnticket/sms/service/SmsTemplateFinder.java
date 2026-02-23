package kr.co.winnticket.sms.service;

import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.mapper.ProductSmsTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SmsTemplateFinder {

    private final ProductSmsTemplateMapper mapper;

    public ProductSmsTemplateDto findTemplate(UUID productId, SmsTemplateCode code) {

        // 1. 상품별 템플릿 우선
        List<ProductSmsTemplateDto> list = mapper.selectByProductId(productId);

        return list.stream()
                .filter(t -> t.getTemplateCode() == code)
                .findFirst()
                // 2. 없으면 기본 템플릿
                .orElseGet(() ->
                        mapper.selectDefaultTemplate(code)
                );
    }

    public ProductSmsTemplateDto findDefaultTemplate(SmsTemplateCode code) {
        return mapper.selectDefaultTemplate(code);
    }
}
