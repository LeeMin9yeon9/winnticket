package kr.co.winnticket.community.event.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[이벤트 > 이벤트 등록] EventPostReqDto")
public class EventPostReqDto {
    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;

    @NotEmpty
    @Schema(description = "작성자")
    private String authorName;

    @NotEmpty
    @Schema(description = "활성화여부")
    private boolean isActive;

    @NotEmpty
    @Schema(description = "이벤트종료일")
    private LocalDate eventEndDate;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
