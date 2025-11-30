package kr.co.winnticket.product.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.product.admin.dto.*;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper mapper;
    private final ObjectMapper objectMapper;

    // 상품 목록 조회
    public List<ProductListGetResDto> selectProductList(String srchWord, UUID categoryId, String salesStatus) {
        List<ProductListGetResDto> lModel = mapper.selectProductList(srchWord, categoryId, salesStatus);

        return lModel;
    }

    // 상품 상세조회
    public ProductDetailGetResDto selectProductDetail(UUID auId) throws Exception {
        ProductDetailGetResDto model = mapper.selectProductDetail(auId);

        if (model.getDetailImages() != null && !model.getDetailImages().isEmpty()) {
            List<String> images = objectMapper.readValue(
                    model.getDetailImages(),
                    new TypeReference<List<String>>() {
                    }
            );
            model.setDetailImagesList(images);
        } else {
                model.setDetailImagesList(new ArrayList<>());
        }

        List<ProductOptionGetResDto> options = mapper.selectOptions(auId);

        for (ProductOptionGetResDto option : options) {
            List<ProductOptionValueGetResDto> values = mapper.selectOptionValues(option.getId());
            option.setValues(values);
        }

        model.setOptions(options);
        model.setSections(mapper.selectSections(auId));

        return model;
    }

    // 상품 등록
    public void insertProduct(ProductPostReqDto model) {
        mapper.insertProduct(model);
    }

    // 상품 기본정보 수정
    public void updateProductBasic(UUID auId, ProductBasicPatchReqDto model) {
        mapper.updateProductBasic(auId, model);
    }

    // 상품 배송정보 수정
    public void updateProductShipping(UUID auId, ProductShippingPatchReqDto model) {
        mapper.updateProductShipping(auId, model);
    }

    // 상품 섹션정보 수정
    public void updateProductSection(UUID auId, ProductSectionPatchReqDto model) {
        mapper.updateProductSection(auId, model.getSectionId(), model.isVisible());
    }

    // 상품 상세내용 수정
    public void updateProductDetailContent(UUID auId, ProductDetailContentPatchReqDto model) throws Exception{
        String imagesJson = null;
        if (model.getDetailImagesList() != null) {
            imagesJson = objectMapper.writeValueAsString(model.getDetailImagesList());
        }

        mapper.updateProductDetailContent(auId, model.getDetailContent(), imagesJson);
    }

    // 상품 옵션 상세 조회
    public ProductOptionGetResDto selectProductOptionDetail(UUID auId) {
        ProductOptionGetResDto model = mapper.selectProductOptionDetail(auId);
        model.setValues(mapper.selectOptionValues(auId));

        return model;
    }

    // 상품 옵션등록
    public void insertProductOption(UUID auId, ProductOptionPostReqDto model) {
        mapper.insertProductOption(auId, model);

        if (model.getValuesInsert() != null && !model.getValuesInsert().isEmpty()) {
            for (ProductOptionValuePostReqDto valueDto : model.getValuesInsert()) {
                mapper.insertOptionValue(model.getOptionId(), valueDto);
            }
        };

        if (model.getDeleteValueIds() != null && !model.getDeleteValueIds().isEmpty()) {
            mapper.deleteOptionValues(model.getDeleteValueIds());
        }
    }
}
