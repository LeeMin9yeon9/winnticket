package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "[파트너 관리 >  현장관리자 응답 DTO ] FieldManagerResDto")
public class FieldManagerResDto {

    @Hidden
    @Schema(description = "현장관리자 ID")
    private UUID id;

    @NotBlank
    @Schema(description = "로그인 ID")
    private String userName;

    @NotBlank
    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "현장관리자 이름")
    private String name;

    @Schema(description = "현장관리자 이메일")
    private String email;

    @Schema(description = "현장관리자 전화번호")
    private String phone;


    @Hidden
    @Schema(description = "파트너ID")
    private UUID partnerId;

    @Schema(description = "파트너명")
    private String partnerName;

    @Schema(description = "활성여부")
    private boolean active;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "ROLE002")
    private List<Long> roleIds;




}
