package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.PostType;
import kr.co.winnticket.common.enums.QnaStatus;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 목록 조회] QnaListGetResDto")
public class QnaListGetResDto {
    @NotEmpty
    @Schema(description = "게시글_ID")
    private UUID id;

    @Schema(description = "타입")
    private PostType type;

    @Schema(description = "QNA상태")
    private QnaStatus status;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "작성자")
    private String authorName;

    @Schema(description = "작성일")
    private String createdAt;

    @Schema(description = "조회수")
    private String views;

    @Schema(description = "차단여부")
    private boolean isBlocked;
}
