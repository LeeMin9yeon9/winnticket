package kr.co.winnticket.siteinfo.terms.mapper;

import kr.co.winnticket.siteinfo.terms.dto.TermsReqDto;
import kr.co.winnticket.siteinfo.terms.dto.TermsResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TermsMapper {
    // 전체 조회
    List<TermsResDto> findAll();

    // 노출
    List<TermsResDto> findVisible();

    // 필수
    List<TermsResDto> findRequired();

    // 단건
    TermsResDto findById(@Param("id") Long id);

    // 등록
    void insert(@Param("req") TermsReqDto req,
                @Param("createdBy") String createdBy);
    // 수정
    void update(@Param("id") Long id,
                @Param("req") TermsReqDto req,
                @Param("updatedBy") String updatedBy);

    // 삭제
    void delete(@Param("id") Long id);

    // 존재확인
    boolean exists(@Param("id") Long id);

    // 표시순서 밀기
    void increaseDisplayOrder(@Param("displayOrder") Integer displayOrder);

    // 삭제 시 순서 당기기
    void decreaseDisplayOrder(@Param("displayOrder") Integer displayOrder);
}
