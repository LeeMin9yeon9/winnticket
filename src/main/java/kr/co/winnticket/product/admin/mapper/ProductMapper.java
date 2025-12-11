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
            @Param("detailContent") String detailContent
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
            @Param("list") List<UUID> ids
    );

    // 상품별 옵션 수정
    void updateProductOption(
            @Param("optionId") UUID auId,
            @Param("model") ProductOptionPatchReqDto model
    );

    // 상품별 옵션별 옵션값 전체삭제
    void deleteOptionValuesByOptionId(UUID auId);

    // 상품별 옵션 삭제
    void deleteProductOption(UUID auId);

    // 섹션 목록 조회
    List<SectionListGetResDto> selectSectionList();

    // 활성화 섹션 목록 조회
    List<SectionListActiveGetResDto> selectSectionListActive();

    // 섹션 상세 조회
    SectionDetailGetResDto selectSectionDetail(
            @Param("id") UUID auId
    );

    // 섹션 등록
    void insertSection(SectionPostReqDto model);

    // 섹션 수정
    void updateSection(
            @Param("id") UUID auId,
            @Param("model") SectionPatchReqDto model
    );

    // 섹션 삭제
    void deleteSection(
            @Param("id") UUID auId
    );

    // Max Order Select
    int getMaxDisplayOrder();

    // --- display_order 관련 쿼리 ---
    void shiftDisplayOrderForInsert(
            @Param("newOrder") int newOrder
    );

    void shiftDisplayOrderForMoveUp(
            @Param("newOrder") int newOrder,
            @Param("oldOrder") int oldOrder
    );

    void shiftDisplayOrderForMoveDown(
            @Param("newOrder") int newOrder,
            @Param("oldOrder") int oldOrder
    );

    // 전체 재정렬(삭제 시 사용)
    void reorderAllDisplayOrders();
}
