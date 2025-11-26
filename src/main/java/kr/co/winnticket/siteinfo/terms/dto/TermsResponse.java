package kr.co.winnticket.siteinfo.terms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsResponse {
    private Long id;
    private String title;
    private String content;
    private Boolean required;
    private Integer displayOrder;
    private Boolean visible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}