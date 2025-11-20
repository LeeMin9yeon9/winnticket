package kr.co.winnticket.community.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.PostType;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[이벤트 > 이벤트 목록 조회] EventListGetResDto")
public class EventListGetResDto {
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

    @Schema(description = "활성화여부")
    private boolean isActive;
}
