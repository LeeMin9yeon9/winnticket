package kr.co.winnticket.product.shop.mapper;

import kr.co.winnticket.product.shop.dto.ProductShopDetailGetResDto;
import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductShopMapper {
    // 상품 목록 검색
    List<ProductShopListGetResDto> selectProductListSearch(
            @Param("name") String name
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
