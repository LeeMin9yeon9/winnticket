package kr.co.winnticket.community.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString(callSuper = true)
@Schema(title = "[이벤트 > 이벤트 수정] EventPatchReqDto")
public class EventPatchReqDto {
    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;

    @NotNull
    @Schema(description = "이벤트종료일")
    private LocalDate eventEndDate;
}
