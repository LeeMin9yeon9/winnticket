package kr.co.winnticket.product.admin.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.mapper.ProductSmsTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductSmsTemplateService {

    private final ProductSmsTemplateMapper mapper;

    // 상품별 템플릿 조회
    public List<ProductSmsTemplateDto> getTemplates(UUID auId) {
        return mapper.selectByProductId(auId);
    }

    // 기본 템플릿 조회
    public ProductSmsTemplateDto getDefaultTemplate(SmsTemplateCode code) {
        return mapper.selectDefaultTemplate(code);
    }

    // 템플릿 수정
    @Transactional
    public void saveTemplates(UUID auId, List<ProductSmsTemplateDto> templates) {
        for (ProductSmsTemplateDto t : templates) {
            mapper.upsertTemplate(
                    auId,
                    t.getTemplateCode(),
                    t.getTitle(),
                    t.getContent()
            );
        }
    }
}