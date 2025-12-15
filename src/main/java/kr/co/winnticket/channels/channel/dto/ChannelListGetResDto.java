package kr.co.winnticket.channels.channel.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[채널관리 > 채널 목록 ] ChannelListGetResDto")
public class ChannelListGetResDto {

    @Hidden
    @Schema(description = "채널아이디")
    private UUID id;

    @Schema(description = "채널코드")
    private String code;

    @NotBlank
    @Schema(description = "채널 이름")
    private String name;

    @Schema(description = "채널 로고")
    private String logoUrl;

    @NotBlank
    @Schema(description = "회사명")
    private String companyName;

    @Schema(description = "활 / 비활성화")
    private boolean visible;

    @NotBlank
    @Schema(description = "도메인 URL")
    private String domain;


}
