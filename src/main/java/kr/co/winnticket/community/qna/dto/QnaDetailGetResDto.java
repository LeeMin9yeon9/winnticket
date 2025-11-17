package kr.co.winnticket.community.qna.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.PostType;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[QNA > QNA 상세 조회] QnaDetailGetResDto")
public class QnaDetailGetResDto {
    @NotEmpty
    @Schema(description = "게시글_ID")
    private UUID id;

    @Schema(description = "타입")
    private PostType type;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "작성자")
    private String authorName;

    @Schema(description = "작성일")
    private String createdAt;

    @Schema(description = "조회수")
    private String views;

    @Schema(description = "내용")
    private String content;
}
