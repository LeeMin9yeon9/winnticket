package kr.co.winnticket.product.shop.service;

import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.product.shop.dto.*;
import kr.co.winnticket.product.shop.mapper.ProductShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductShopService {
    private final ProductShopMapper mapper;

    // 상품 목록 검색
    public List<ProductShopListGetResDto> selectProductListSearch(String name, UUID channelId) {
        List<ProductShopListGetResDto> lModel = mapper.selectProductListSearch(name, channelId);
        return lModel;
    }

    // 상품 목록 조회
    public ShopMainResDto selectProductList(String mainCategory, String subCategory, UUID channelId) {

        // 카테고리 없는 경우 → 메인 페이지
        if (mainCategory == null && subCategory == null) {

            List<ProductSectionListGetResDto> sections = mapper.selectSection(channelId);

            for (ProductSectionListGetResDto section : sections) {
                List<ProductSectionProductGetResDto> sectionProducts =
                        mapper.selectSectionProduct(section.getSectionId(), channelId);
                section.setProducts(sectionProducts);
            }

            List<ProductShopListGetResDto> allProducts =
                    mapper.selectProductList(null, null, channelId);

            return new ShopMainResDto(sections, allProducts);
        }

        // 카테고리 있는 경우 → 상품 목록만
        List<ProductShopListGetResDto> products =
                mapper.selectProductList(mainCategory, subCategory, channelId);

        return new ShopMainResDto(List.of(), products);
    }

    // 상품 상세 조회
    public ProductShopDetailGetResDto selectProductDetail(String code, UUID channelId) {
        ProductShopDetailGetResDto model = mapper.selectProductDetail(code, channelId);

        if (model == null) {
            return null;
        }

        UUID productId = model.getId();

        List<ProductShopOptionGetResDto> options =
                mapper.selectShopOptions(productId);

        for (ProductShopOptionGetResDto option : options) {

            List<ProductShopOptionValueGetResDto> values =
                    mapper.selectShopOptionValues(channelId, option.getId());

            option.setValues(values);
        }

        model.setOptions(options);

        if (model.getType() == ProductType.STAY) {
            List<ProductDatePriceGetResDto> datePrice =  mapper.selectStayDatePrices(code);
            model.setDatePrices(datePrice);
        }

        return model;
    }
}
