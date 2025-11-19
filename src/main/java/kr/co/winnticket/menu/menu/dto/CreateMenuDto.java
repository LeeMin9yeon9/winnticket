package kr.co.winnticket.menu.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMenuDto implements OrderUpdateble {
    @Schema(description = "메뉴_ID")
    private UUID id;

    @Schema(description = "메뉴명")
    private String name;

    @Schema(description = "메뉴_코드")
    private String code;

    @Schema(description = "메뉴_레벨_트리구조")
    private Integer level;

    @Schema(description = "메뉴_레벨_하위ID")
    private UUID parentId;

    @Schema(description = "메뉴_표시순서")
    private Integer displayOrder;

    @Schema(description = "메뉴_활성화여부")
    private Boolean visible;

    @Schema(description = "메뉴_아이콘")
    private String iconUrl;

    @Schema(description = "메뉴_라우팅")
    private String routePath;
}
