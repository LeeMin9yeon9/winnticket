package kr.co.winnticket.menu.menu.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMenuDto {
    private UUID id;
    private String name;
    private String code;
    private Integer level;
    private UUID parentId;
    private Integer displayOrder;
    private Boolean visible;
    private String iconUrl;
    private String routePath;
}
