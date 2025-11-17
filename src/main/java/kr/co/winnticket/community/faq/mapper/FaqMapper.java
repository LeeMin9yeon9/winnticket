package kr.co.winnticket.community.faq.mapper;

import kr.co.winnticket.community.faq.dto.FaqDetailGetResDto;
import kr.co.winnticket.community.faq.dto.FaqListGetResDto;
import kr.co.winnticket.community.faq.dto.FaqPostReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface FaqMapper {
    // FAQ 목록 조회
    List<FaqListGetResDto> selectFaqList(
            @Param("title") String asTitle,
            @Param("begDate") String asBegDate,
            @Param("endDate") String asEndDate
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
        @Param("content") String content
    );

    // FAQ 삭제
    void deleteFaq(
        @Param("id") UUID auId
    );
}