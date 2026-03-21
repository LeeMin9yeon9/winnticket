package kr.co.winnticket.siteinfo.terms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[약관 응답 DTO] TermsResDto")
public class TermsResDto {

    private Long id;

    @Schema(description = "약관 제목")
    private String title;


    @Schema(description = "약관 내용")
    private String content;

    @Schema(description = "필수 여부")
    private Boolean required;

    @Schema(description = "표시 순서")
    private Integer displayOrder;

    @Schema(description = "노출 여부")
    private Boolean visible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;
}