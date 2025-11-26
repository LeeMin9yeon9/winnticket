package kr.co.winnticket.product.admin.mapper;

import kr.co.winnticket.product.admin.dto.ProductPostReqDto;
import kr.co.winnticket.product.admin.dto.ProductListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProductMapper {
    // 상품 목록 조회
    List<ProductListGetResDto> selectProductList(
            @Param("srchWord") String srchWord,
            @Param("categoryId") UUID categoryId,
            @Param("salesStatus") String salesStatus
    );

    // 상품 등록
    void insertProduct(ProductPostReqDto model);
}
