package kr.co.winnticket.community.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[공지사항 > 공지사항 수정] NoticePatchReqDto")
public class NoticePatchReqDto {
    @NotEmpty
    @Schema(description = "게시글_ID")
    private UUID id;

    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;
}
