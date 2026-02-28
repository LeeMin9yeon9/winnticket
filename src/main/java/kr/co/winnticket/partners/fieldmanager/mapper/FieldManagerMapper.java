package kr.co.winnticket.partners.fieldmanager.mapper;

import kr.co.winnticket.partners.fieldmanager.dto.FieldManagerInsertPostDto;
import kr.co.winnticket.partners.fieldmanager.dto.FieldManagerListGetResDto;
import kr.co.winnticket.partners.fieldmanager.dto.FieldManagerResDto;
import kr.co.winnticket.partners.fieldmanager.dto.UpdateFieldManagerDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface FieldManagerMapper {

    // 현장 관리자 목록
    List<FieldManagerListGetResDto> getListByPartner(
            @Param("partnerId") String partnerId
    );

    // 현장관리자 상세조회
    FieldManagerResDto getDetail(
            @Param("id") UUID id,
            @Param("partnerId") UUID partnerId
    );

    // 현장관리자 추가
    void insert(
            @Param("id") UUID id,
            @Param("dto") FieldManagerInsertPostDto dto
    );

    // 현장관리자 수정
    void update(
            @Param("partnerId") UUID partnerId,
            @Param("id") UUID Id,
            @Param("model")UpdateFieldManagerDto model
    );

    // 현장관리자 패스워드 변경조회
    String getPassword(
            @Param("partnerId") UUID partnerId,
            @Param("id") UUID id
    );

    // 현장관리자 패스워드 변경
    int updatePassword(
            @Param("partnerId") UUID partnerId,
            @Param("id") UUID id,
            @Param("newPassword") String newPassword
    );

    //현장관리자 패스워드 리셋
    void resetPassword(
            @Param("partnerId") UUID partnerId,
            @Param("id") UUID id,
            @Param("newPassword") String newPassword
    );

    // 현장관리자 삭제
    void delete(
            @Param("partnerId") UUID partnerId,
            @Param("id") UUID id
    );

    // 현장관리자 ID중복체크
    boolean existsByAccountId(
            @Param("accountId") String accountId);

    // 수정 시 본인 제외 ID 중복 체크
    boolean existsByAccountIdExcludeId(
            @Param("accountId") String accountId,
            @Param("id") UUID id
    );

}
