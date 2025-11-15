package kr.co.winnticket.community.notice.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[공지사항 > 공지사항등록] NoticePostReqDto")
public class NoticePostReqDto {
    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
