package kr.co.winnticket.product.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.service.FileService;
import kr.co.winnticket.product.admin.dto.*;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper mapper;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    // 상품 목록 조회
    public List<ProductListGetResDto> selectProductList(String srchWord, UUID categoryId, String salesStatus) {
        List<ProductListGetResDto> lModel = mapper.selectProductList(srchWord, categoryId, salesStatus);

        return lModel;
    }

    // 상품 상세조회
    public ProductDetailGetResDto selectProductDetail(UUID auId) throws Exception {
        ProductDetailGetResDto model = mapper.selectProductDetail(auId);
        List<ProductOptionGetResDto> options = mapper.selectOptions(auId);

        for (ProductOptionGetResDto option : options) {
            List<ProductOptionValueGetResDto> values = mapper.selectOptionValues(option.getId());
            option.setValues(values);
        }

        model.setOptions(options);
        model.setSections(mapper.selectSections(auId));

        if (model.getType() == ProductType.STAY) {
            List<ProductPeriodGetResDto> periods = mapper.selectProductPeriods(auId);
            model.setPeriods(periods);
        }

        return model;
    }

    // 상품 등록
    @Transactional
    public void insertProduct(ProductPostReqDto model) throws Exception {
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
    @Transactional
    public void updateProductDetailContent(UUID auId, ProductDetailContentPatchReqDto model) throws Exception{
        mapper.updateProductDetailContent(auId, model.getDetailContent());
    }

    // 상품 활성화여부 수정
    public void updateProductVisible(UUID auId, boolean visible) {
        mapper.updateProductVisible(auId, visible);
    }

     // 상품 삭제
    public void deleteProduct(UUID auId) {
        mapper.deleteProduct(auId);
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

    // 상품 옵션 수정
    public void updateProductOption(UUID auId, ProductOptionPatchReqDto model) {
        mapper.updateProductOption(auId, model);

        if (model.getValuesInsert() != null && !model.getValuesInsert().isEmpty()) {
            for (ProductOptionValuePostReqDto valueDto : model.getValuesInsert()) {
                mapper.insertOptionValue(auId, valueDto);
            }
        };

        if (model.getDeleteValueIds() != null && !model.getDeleteValueIds().isEmpty()) {
            mapper.deleteOptionValues(model.getDeleteValueIds());
        }
    }

    // 상품 옵션 삭제
    public void deleteProductOption(UUID auId) {
        mapper.deleteOptionValuesByOptionId(auId);
        mapper.deleteProductOption(auId);
    }

    // 상품 기간등록
    public void insertProductPeriod(ProductPeriodPostReqDto model) {
        int groupNo = mapper.selectNextGroupNo(model.getOptionValueId());
        model.setGroupNo(groupNo);
        mapper.insertProductPeriod(model);
    }

    // 섹션 목록 조회
    public List<SectionListGetResDto> selectSectionList() {
        List<SectionListGetResDto> lModel = mapper.selectSectionList();

        return lModel;
    }

    // 활성화 섹션 목록 조회
    public List<SectionListActiveGetResDto> selectSectionListActive() {
        List<SectionListActiveGetResDto> lModel = mapper.selectSectionListActive();

        return lModel;
    }

    // 섹션 상세조회
    public SectionDetailGetResDto selectSectionDetail(UUID auId) {
        SectionDetailGetResDto model = mapper.selectSectionDetail(auId);

        return model;
    }

    // Max Order Select
    private int getMaxDisplayOrder() {
        return mapper.getMaxDisplayOrder();
    }

    // 섹션등록
    @Transactional
    public void insertSection(SectionPostReqDto model) {
        int maxOrder = getMaxDisplayOrder();         // ex: 현재 1,2,3 이면 maxOrder=3
        int newOrder = model.getDisplayOrder();            // 요청 displayOrder

        // displayOrder가 max+1 보다 크면 max+1로 조정
        if (newOrder > maxOrder + 1) {
            newOrder = maxOrder + 1;
        }

        // Insert 위치 이상은 모두 뒤로 밀기
        mapper.shiftDisplayOrderForInsert(newOrder);

        model.setDisplayOrder(newOrder);
        mapper.insertSection(model);
    }

    // 섹션 수정
    @Transactional
    public void updateSection(UUID auId, SectionPatchReqDto model) {
        SectionDetailGetResDto origin = mapper.selectSectionDetail(auId);

        int oldOrder = origin.getDisplayOrder();
        int newOrder = model.getDisplayOrder() != null ? model.getDisplayOrder() : oldOrder;
        int maxOrder = getMaxDisplayOrder();

        if (newOrder > maxOrder) {
            newOrder = maxOrder;
        }

        if (newOrder != oldOrder) {
            if (newOrder < oldOrder) {
                mapper.shiftDisplayOrderForMoveUp(newOrder, oldOrder);
            } else {
                mapper.shiftDisplayOrderForMoveDown(newOrder, oldOrder);
            }
        }

        model.setDisplayOrder(newOrder);

        mapper.updateSection(auId, model);
    }

    // 섹션 삭제
    @Transactional
    public void deleteSection(UUID auId) {
        mapper.deleteSection(auId);
        mapper.reorderAllDisplayOrders();
    }

    public void deleteProductPeriod(UUID auId, int groupNo) {
        mapper.deleteProductPeriod(auId, groupNo);
    }
}
