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
            @Param("id") UUID id
    );

    // 현장관리자 추가
    void insert(
            @Param("id") UUID id,
            @Param("model") FieldManagerInsertPostDto model
    );

    // 현장관리자 수정
    void update(
            @Param("id") UUID Id,
            @Param("model")UpdateFieldManagerDto model
    );

    // 현장관리자 패스워드 변경조회
    String getPassword(UUID id);

    // 현장관리자 패스워드 변경
    int updatePassword(
            @Param("id") UUID id,
            @Param("newPassword") String newPassword
    );

    //현장관리자 패스워드 리셋
    void resetPassword(
            @Param("id") UUID id,
            @Param("newPassword") String newPassword
    );

    // 현장관리자 삭제
    void delete(
            @Param("id") String id
    );
}
