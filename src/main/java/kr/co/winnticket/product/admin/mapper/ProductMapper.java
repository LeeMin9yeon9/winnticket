package kr.co.winnticket.product.admin.mapper;

import kr.co.winnticket.product.admin.dto.*;
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

    // 상품 상세정보 조회
    ProductDetailGetResDto selectProductDetail(
            @Param("id") UUID auId
    );

    // 상품별 섹션목록조회
    List<ProductSectionGetResDto> selectSections(
            @Param("id") UUID auId
    );

    // 상품별 옵션목록조회
    List<ProductOptionGetResDto> selectOptions(
            @Param("id") UUID auId
    );

    // 상품별 옵션별 가격조회
    List<ProductOptionValueGetResDto> selectOptionValues(UUID id);

    // 상품 등록
    void insertProduct(ProductPostReqDto model);

    // 상품 기본정보 수정
    void updateProductBasic(
            @Param("id") UUID auId,
            @Param("model") ProductBasicPatchReqDto model
    );

    // 상품 배송정보 수정
    void updateProductShipping(
            @Param("id") UUID auId,
            @Param("model") ProductShippingPatchReqDto model
    );

    // 상품 섹션정보 수정
    void updateProductSection(
            @Param("id") UUID auId,
            @Param("sectionId") UUID sectionId,
            @Param("visible") boolean visible
    );

    // 상품 상세내용 수정
    void updateProductDetailContent(
            @Param("id") UUID id,
            @Param("detailContent") String detailContent,
            @Param("detailImages") String detailImages
    );

    // 상품 옵션상세 조회
    ProductOptionGetResDto selectProductOptionDetail(
            @Param("id") UUID auId
    );

    // 상품옵션등록
    void insertProductOption(
            @Param("id") UUID auId,
            @Param("model") ProductOptionPostReqDto model
    );

    // 상품별 옵션별 옵션값 등록
    void insertOptionValue(
            @Param("optionId") UUID optionId,
            @Param("model") ProductOptionValuePostReqDto valueDto
    );

    // 상품별 옵션별 옵션값 삭제
    void deleteOptionValues(
            @Param("optionValueId") List<UUID> deleteValueIds
    );
}
