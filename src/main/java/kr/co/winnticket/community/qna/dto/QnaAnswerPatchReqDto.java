package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 답변 등록] QnaAnswerPatchReqDto")
public class QnaAnswerPatchReqDto {
    @NotEmpty
    @Schema(description = "답변")
    private String answer;

    @NotEmpty
    @Schema(description = "답변자")
    private String answeredBy;
}
