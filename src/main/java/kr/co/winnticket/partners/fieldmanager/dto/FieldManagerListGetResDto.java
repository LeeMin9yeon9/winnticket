package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(title = "[파트너 관리 >  현장관리자 목록 ] FieldManagerListGetResDto")
public class FieldManagerListGetResDto {

    @Hidden
    @Schema(description = "ID")
    private UUID id;

    @Schema(description = "현장관리자 ID")
    private String userName;

    @NotBlank
    @Schema(description = "현장관리자 이름")
    private String name;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "연락처")
    private String phone;

    @Schema(description = "활성여부")
    private boolean active;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;


}
