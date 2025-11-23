package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
// 로그인 시 사용자 정보 DTO(응답용 user)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDto {
    @Schema(description = "PK ID")
    private String id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "직원: accountId / 현장관리자: username")
    private String accountId;

    @Schema(description = "역할")
    private String roleId;

    @Schema(description = "이미지")
    private String avatarUrl;

    @Schema(description = "로그인 구분 필드 employee / field-manager ")
    private String userType;

    @Schema(description = "현장관리자인 경우만 존재")
    private String partnerId;


}
