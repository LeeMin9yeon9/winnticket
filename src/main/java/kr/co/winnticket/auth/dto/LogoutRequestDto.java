package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "로그아웃 요청 DTO")
public class LogoutRequestDto {

    @Schema(description = "refreshToken")
    private String refreshToken;
}
