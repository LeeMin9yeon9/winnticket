package kr.co.winnticket.siteinfo.terms.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsRequest {
    private String title;
    private String content;
    private Boolean required;
    private Integer displayOrder;
    private Boolean visible;
}