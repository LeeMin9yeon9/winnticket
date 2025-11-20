package kr.co.winnticket.community.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.PostType;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[이벤트 > 이벤트 상세 조회] EventDetailGetResDto")
public class EventDetailGetResDto {
    @NotNull
    @Schema(description = "게시글_ID")
    private UUID id;

    @Schema(description = "타입")
    private PostType type;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "작성자")
    private String authorName;

    @Schema(description = "작성일")
    private LocalDate createdAt;

    @Schema(description = "조회수")
    private int views;

    @Schema(description = "내용")
    private String content;
}
