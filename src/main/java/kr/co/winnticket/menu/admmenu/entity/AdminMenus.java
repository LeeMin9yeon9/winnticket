package kr.co.winnticket.menu.admmenu.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMenus {
    private UUID id;
    private String title;
    private String titleEn;
    private String icon;
    private String page;
    private Integer displayOrder;
    private Boolean visible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
