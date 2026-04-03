package kr.co.winnticket.product.shop.service;

import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.product.shop.dto.*;
import kr.co.winnticket.product.shop.mapper.ProductShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductShopService {
    private final ProductShopMapper mapper;

    @Transactional(readOnly = true)
    public List<ProductShopListGetResDto> selectProductListSearch(String name, UUID channelId) {
        return mapper.selectProductListSearch(name, channelId);
    }

    @Transactional(readOnly = true)
    public ShopMainResDto selectProductList(String mainCategory, String subCategory, UUID channelId) {

        if (mainCategory == null && subCategory == null) {
            List<ProductSectionListGetResDto> sections = mapper.selectSection(channelId);

            for (ProductSectionListGetResDto section : sections) {
                List<ProductSectionProductGetResDto> sectionProducts =
                        mapper.selectSectionProduct(section.getSectionId(), channelId);
                section.setProducts(sectionProducts);
            }

            // 인기상품 섹션 (판매량 기준 상위 10개) - 항상 첫 번째 섹션으로 표시
            List<ProductSectionProductGetResDto> popularProducts =
                    mapper.selectPopularProducts(channelId);

            if (!popularProducts.isEmpty()) {
                ProductSectionListGetResDto popularSection = new ProductSectionListGetResDto();
                popularSection.setSectionId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
                popularSection.setSectionCode("POPULAR");
                popularSection.setSectionName("실시간 베스트 상품");
                popularSection.setProducts(popularProducts);

                List<ProductSectionListGetResDto> allSections = new ArrayList<>();
                allSections.add(popularSection);
                allSections.addAll(sections);
                sections = allSections;
            }

            List<ProductShopListGetResDto> allProducts =
                    mapper.selectProductList(null, null, channelId);

            return new ShopMainResDto(sections, allProducts);
        }

        List<ProductShopListGetResDto> products =
                mapper.selectProductList(mainCategory, subCategory, channelId);

        return new ShopMainResDto(List.of(), products);
    }

    @Transactional(readOnly = true)
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
            List<ProductDatePriceGetResDto> datePrice = mapper.selectStayDatePrices(code);
            model.setDatePrices(datePrice);
        }

        return model;
    }
}
