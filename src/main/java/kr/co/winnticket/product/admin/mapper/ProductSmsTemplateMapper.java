package kr.co.winnticket.product.admin.mapper;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;
@Mapper
public interface ProductSmsTemplateMapper {

    // 상품별 템플릿 조회
    List<ProductSmsTemplateDto> selectByProductId(UUID auId);

    // 기본 템플릿 조회
    ProductSmsTemplateDto selectDefaultTemplate(SmsTemplateCode code);

    // 템플릿 수정
    void upsertTemplate(
            @Param("id") UUID auId,
            @Param("code") SmsTemplateCode code,
            @Param("title") String title,
            @Param("content") String content
    );
}
