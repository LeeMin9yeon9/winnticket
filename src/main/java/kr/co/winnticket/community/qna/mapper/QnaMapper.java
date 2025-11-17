package kr.co.winnticket.community.qna.mapper;

import kr.co.winnticket.community.qna.dto.QnaDetailGetResDto;
import kr.co.winnticket.community.qna.dto.QnaListGetResDto;
import kr.co.winnticket.community.qna.dto.QnaPostReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface QnaMapper {
    // QNA 목록 조회
    List<QnaListGetResDto> selectQnaList(
            @Param("title") String asTitle,
            @Param("begDate") String asBegDate,
            @Param("endDate") String asEndDate
    );

    // QNA 상세 조회
    QnaDetailGetResDto selectQnaDetail(
            @Param("id") UUID auId
    );

    // QNA 등록
    void insertQna(QnaPostReqDto model);

    // QNA 수정
    void updateQna(
        @Param("id") UUID auId, 
        @Param("title") String title,
        @Param("content") String content
    );

    // QNA 삭제
    void deleteQna(
        @Param("id") UUID auId
    );
}