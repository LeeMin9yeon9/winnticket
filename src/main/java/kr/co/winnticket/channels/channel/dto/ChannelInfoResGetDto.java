package kr.co.winnticket.channels.channel.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[채널관리 > 채널 기본정보 ] ChannelInfoResDto")
public class ChannelInfoResGetDto {

    @Hidden
    @Schema(description = "채널아이디")
    private UUID id;

    @Pattern(regexp = "^[A-Z0-9]+$", message = "채널코드는 대문자와 숫자만 입력 가능합니다.")
    @Schema(description = "채널코드")
    private String code;

    @Schema(description = "채널 이름")
    private String name;

    @Schema(description = "회사명")
    private String companyName;

    @Schema(description = "수수료율")
    private Integer commissionRate;

    @Schema(description = "활성 여부")
    private Boolean visible;

    @Schema(description = "채널 설명")
    private String description;

    @Schema(description = "채널 로고")
    private String logoUrl;

    @Schema(description = "파비콘 URL")
    private String faviconUrl;

    @Schema(description = "연락처 이메일")
    private String email;

    @Schema(description = "연락처 전화번호")
    private String phone;

    @Schema(description = "웹사이트 URL")
    private String domain;

    @Schema(description = "생성일")
    private String createdAt;

    @Schema(description = "수정일")
    private String updatedAt;

    @Schema(description = "카드결제 사용 여부",example = "false")
    private Boolean useCard;

    @Schema(description = "포인트 사용 여부" ,example = "false")
    private Boolean usePoint;



}
