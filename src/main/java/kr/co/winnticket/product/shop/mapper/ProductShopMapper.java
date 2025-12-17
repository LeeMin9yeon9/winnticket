package kr.co.winnticket.product.shop.mapper;

import kr.co.winnticket.product.shop.dto.ProductSectionListGetResDto;
import kr.co.winnticket.product.shop.dto.ProductSectionProductGetResDto;
import kr.co.winnticket.product.shop.dto.ProductShopDetailGetResDto;
import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProductShopMapper {

    // 세션 목록 조회
    List<ProductSectionListGetResDto> selectSection();

    // 상품 목록 검색
    List<ProductShopListGetResDto> selectProductListSearch(
            @Param("name") String name
    );

    // 섹션별 상품 조회
    List<ProductSectionProductGetResDto> selectSectionProduct(
            @Param("sectionId") UUID sectionId
    );

    // 상품 목록 조회
    List<ProductShopListGetResDto> selectProductList(
            @Param("mainCategory") String mainCategory,
            @Param("subCategory") String subCategory
    );

    // 상품 상세 조회
    ProductShopDetailGetResDto selectProductDetail(
            @Param("code") String code
    );
}
