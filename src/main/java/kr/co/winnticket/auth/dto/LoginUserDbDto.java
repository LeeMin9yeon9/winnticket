package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

// service 내부 및 db 매핑용
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "로그인 조회 내부 DTO")
public class LoginUserDbDto {
    @Schema(description = "사용자 PK ID")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "직원: accountId / 현장관리자: username")
    private String accountId;

    @Schema(description = "password")
    private String password;

    @Schema(description = "역할 ROLE001, ROLE002 ")
    private String roleId;

    @Schema(description = "이미지")
    private String avatarUrl;

    @Schema(description = "로그인 구분 필드 employee / field-manager ")
    private String userType;

    @Schema(description = "현장관리자인 경우만 존재")
    private String partnerId;
}
