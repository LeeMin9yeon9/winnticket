package kr.co.winnticket.product.admin.mapper;

import kr.co.winnticket.product.admin.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
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

    // 상품별 옵션별 가격상세조회
    ProductOptionValueGetResDto selectOptionValueDetail(UUID id);

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

    // 상품 활성화여부 수정
    void updateProductVisible(
            @Param("id") UUID auId,
            @Param("visible") boolean visible
    );

    // 상품 삭제
    void deleteProduct(
            @Param("id") UUID auid
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

    // 기간 조회
    List<ProductPeriodGetResDto> selectProductPeriods(
            @Param("id") UUID auId
    );

    // 기간 등록
    void insertProductPeriod(ProductPeriodPostReqDto model);

    // 기간 그룹번호 생성
    int selectNextGroupNo(
            @Param("id") UUID optionValueId
    );

    void deleteProductPeriod(
            @Param("id") UUID auId,
            @Param("groupNo") int groupNo
    );

    // 상품 채널별 할인 목록 조회
    List<ProductChannelDiscountListGetResDto> selectProductChannelDiscountsList(
            @Param("id") UUID auId,
            @Param("channelName") String channelName,
            @Param("status") String status,
            @Param("period") String period
    );

    // 상품 채널별 할인 상세 조회
    ProductChannelDiscountDetailGetResDto selectProductChannelDiscountsDetail(
            @Param("id") UUID auId,
            @Param("discountId") UUID discountId
    );

    // 상품 채널별 할인 등록
    void insertProductChannelDiscount(
            @Param("id") UUID auId,
            @Param("channelId") UUID channelId,
            @Param("originalPrice") int originalPrice,
            @Param("discountRate") int discountRate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isActive") boolean active
    );

    // 상품 채널별 할인 수정
    void updateProductChannelDiscount(
            @Param("id") UUID discountId,
            @Param("discountRate") int discountRate,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 상품 채널별 할인 삭제
    void deleteProductChannelDiscount(
            @Param("id") UUID discountId
    );

    // 상품 채널별 할인 활성화여부 수정
    void updateProductChannelDiscountIsActive(
            @Param("id") UUID discountId,
            @Param("isActive") boolean abIsActive
    );

    // 해당 카테코리 상품 확인
    int countByCategoryId(@Param("categoryId") UUID categoryId);
}