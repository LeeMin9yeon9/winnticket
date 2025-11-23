package kr.co.winnticket.community.faq.mapper;

import kr.co.winnticket.community.faq.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface FaqMapper {
    // FAQ 목록 조회
    List<FaqListGetResDto> selectFaqList(
            @Param("title") String asTitle,
            @Param("begDate") LocalDate asBegDate,
            @Param("endDate") LocalDate asEndDate
    );

    // FAQ 상세 조회
    FaqDetailGetResDto selectFaqDetail(
            @Param("id") UUID auId
    );

    // FAQ 등록
    void insertFaq(FaqPostReqDto model);

    // FAQ 수정
    void updateFaq(
        @Param("id") UUID auId, 
        @Param("title") String title,
        @Param("content") String content,
        @Param("category") UUID category
    );

    // FAQ 삭제
    void deleteFaq(
        @Param("id") UUID auId
    );

    // 카테고리 목록조회
    List<FaqCategoryListGetResDto> selectFaqCategoryList();

    // 카테고리 등록
    void insertFaqCategory(FaqCategoryPostReqDto model);

    // 카테고리 수정
    void updateFaqCategory(
            @Param("id") UUID auId,
            @Param("name") String name
    );

    // 카테고리 삭제
    int deleteFaqCategory(
            @Param("id") UUID auId
    );

    // 카테고리 정렬순서 재정렬
    void reorderDisplayOrder();
}
