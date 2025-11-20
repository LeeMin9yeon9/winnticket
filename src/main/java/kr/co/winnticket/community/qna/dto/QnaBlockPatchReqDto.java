package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 차단] QnaBlockPatchReqDto")
public class QnaBlockPatchReqDto {
    @NotEmpty
    @Schema(description = "차단사유")
    private String blockedReason;

    @NotEmpty
    @Schema(description = "차단자")
    private String blockedBy;
}
