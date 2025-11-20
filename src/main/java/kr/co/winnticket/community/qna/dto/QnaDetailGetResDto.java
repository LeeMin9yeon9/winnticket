package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.QnaStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 상세 조회] QnaDetailGetResDto")
public class QnaDetailGetResDto {
    @NotEmpty
    @Schema(description = "게시글_ID")
    private UUID id;

    @Schema(description = "상태")
    private QnaStatus status;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "작성자")
    private String authorName;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    @Schema(description = "답변")
    private String answer;

    @Schema(description = "답변일시")
    private LocalDateTime answeredAt;

    @Schema(description = "차단여부")
    private boolean isBlocked;

    @Schema(description = "차단사유")
    private String blockedReason;

    @Schema(description = "차단일시")
    private LocalDateTime blockedAt;
}
