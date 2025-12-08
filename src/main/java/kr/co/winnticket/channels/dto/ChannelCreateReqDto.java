package kr.co.winnticket.channels.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[채널관리 > 채널 추가 ] ChannelCreateReqDto")
public class ChannelCreateReqDto {

    @Hidden
    @NotBlank
    @Schema(description = "채널 ID")
    private UUID id;

    @NotBlank
    @Schema(description = "채널 코드")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "채널코드는 대문자와 숫자만 입력 가능합니다.")
    private String code;

    @NotBlank
    @Schema(description = "채널 이름")
    private String name;

    @NotBlank
    @Schema(description = "회사명")
    private String companyName;

    @Schema(description = "수수료율")
    private Integer commissionRate;

    @Schema(description = "로고 URL")
    private String logoUrl;

    @Schema(description = "파비콘 URL")
    private String faviconUrl;

    @Schema(description = "연락처 이메일")
    private String email;

    @Schema(description = "연락처 전화번호")
    private String phone;

    @Schema(description = "웹사이트 URL")
    private String domain;

    @Schema(description = "채널 설명")
    private String description;

    @Schema(description = "활성 여부")
    private Boolean status;



    public void setCode(String code) {
        if (code != null) {
            this.code = code.toUpperCase(); // 소문자로 들어와도 대문자로 변환
        }
    }

}
