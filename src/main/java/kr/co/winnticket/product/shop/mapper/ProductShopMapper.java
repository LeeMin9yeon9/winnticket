package kr.co.winnticket.product.shop.mapper;

import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProductShopMapper {
    // 상품 목록 조회
    List<ProductShopListGetResDto> selectProductList(
            @Param("name") String name,
            @Param("categoryId") UUID categoryId
    );
}
