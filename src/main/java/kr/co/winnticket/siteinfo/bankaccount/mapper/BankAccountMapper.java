package kr.co.winnticket.siteinfo.bankaccount.mapper;

import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountReqDto;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankAccountMapper {

    // 전체 조회
    List<BankAccountResDto> findAll();

    // 노출 계좌
    List<BankAccountResDto> findVisible();

    // 단건 조회
    BankAccountResDto findById(@Param("id") Long id);

    // 계좌 등록
    void insert(@Param("req") BankAccountReqDto request,
                @Param("createdBy") String createdBy);

    // 계좌 수정
    void update(@Param("id") Long id,
                @Param("req") BankAccountReqDto request,
                @Param("updatedBy") String updatedBy);

    // 계좌 삭제
    void delete(@Param("id") Long id);

    // 계좌 활성/비활성 여부
    boolean exists(@Param("id") Long id);

    // 삭제 후 재정렬
    void reorderAfterDelete();

    // 순서 밀기 (앞에서 끼어들기)
    void shiftOrder(@Param("newOrder") Integer newOrder);

    // 기존 자리 당기기
    void decreaseOrderAfter(@Param("oldOrder") Integer oldOrder);

    // 해당 표시순서에 항목 존재 확인
    boolean existsByDisplayOrder(@Param("displayOrder") Integer displayOrder);

    // 해당 표시순서에 항목 존재 확인 (자기 자신 제외)
    boolean existsByDisplayOrderExcluding(@Param("displayOrder") Integer displayOrder, @Param("excludeId") Long excludeId);
}
