package kr.co.winnticket.product.shop.service;

import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.product.shop.dto.*;
import kr.co.winnticket.product.shop.mapper.ProductShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductShopService {
    private final ProductShopMapper mapper;
    private final ProductMapper AdminMapper;

    // 상품 목록 검색
    public List<ProductShopListGetResDto> selectProductListSearch(String name) {
        List<ProductShopListGetResDto> lModel = mapper.selectProductListSearch(name);
        return lModel;
    }

    // 상품 목록 조회
    public ShopMainResDto selectProductList(String mainCategory, String subCategory) {

        // 카테고리 없는 경우 → 메인 페이지
        if (mainCategory == null && subCategory == null) {

            List<ProductSectionListGetResDto> sections = mapper.selectSection();

            for (ProductSectionListGetResDto section : sections) {
                List<ProductSectionProductGetResDto> sectionProducts =
                        mapper.selectSectionProduct(section.getSectionId());
                section.setProducts(sectionProducts);
            }

            List<ProductShopListGetResDto> allProducts =
                    mapper.selectProductList(null, null);

            return new ShopMainResDto(sections, allProducts);
        }

        // 카테고리 있는 경우 → 상품 목록만
        List<ProductShopListGetResDto> products =
                mapper.selectProductList(mainCategory, subCategory);

        return new ShopMainResDto(List.of(), products);
    }

    // 상품 상세 조회
    public ProductShopDetailGetResDto selectProductDetail(String code) {
        ProductShopDetailGetResDto model = mapper.selectProductDetail(code);
        List<ProductOptionGetResDto> options = AdminMapper.selectOptions(model.getId());

        for (ProductOptionGetResDto option : options) {
            List<ProductOptionValueGetResDto> values = AdminMapper.selectOptionValues(option.getId());
            option.setValues(values);
        }

        model.setOptions(options);

        return model;
    }
}
