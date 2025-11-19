package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 답변 수 조회] QnaCntGetResDto")
public class QnaCntGetResDto {
    @Schema(description = "전체답변수")
    private int allCnt;

    @Schema(description = "답변대기수")
    private int pendingCnt;

    @Schema(description = "답변완료수")
    private int answeredCnt;

    @Schema(description = "차단수")
    private int blockedCnt;
}
