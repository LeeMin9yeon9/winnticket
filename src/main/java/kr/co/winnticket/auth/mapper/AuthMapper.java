package kr.co.winnticket.auth.mapper;

import kr.co.winnticket.auth.dto.LoginUserDbDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    // 직원 로그인 조회
   LoginUserDbDto selectEmpAccountId(@Param("accountId")String accountId);

    // 현장관리자 로그인 조회
    LoginUserDbDto selectFieldAccountId(@Param("accountId")String accountId);

    // 현장관리자 마지막 로그인 시간
    int updateLastLoginAt(String id);

    // 현장관리자 비밀번호 해시값 갱신 (평문 → BCrypt 자동 마이그레이션용)
    int updateFieldPasswordHash(@Param("id") String id, @Param("hashed") String hashed);
}
