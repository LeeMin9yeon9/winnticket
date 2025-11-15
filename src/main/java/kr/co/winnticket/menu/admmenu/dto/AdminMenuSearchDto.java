package kr.co.winnticket.menu.admmenu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminMenuSearchDto {
    private String id;
    private String title;
    private String titleEn;
    private String icon;
    private String page;
    private Integer displayOrder;
    private Boolean visible;
}

