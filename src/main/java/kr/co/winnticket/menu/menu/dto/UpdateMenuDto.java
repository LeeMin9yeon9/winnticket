package kr.co.winnticket.menu.menu.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMenuDto implements OrderUpdateble {

    @Hidden
    @Schema(description = "메뉴_ID")
    private UUID id;

    @Schema(description = "쇼핑몰_메뉴명")
    private String name;

    @Schema(description = "쇼핑몰_메뉴코드")
    private String code;

    @Schema(description = "쇼핑몰_메뉴레벨")
    private Integer level;

    @Hidden
    @Schema(description = "쇼핑몰_메뉴하위ID")
    private UUID parentId;

    @Schema(description = "쇼핑몰_메뉴표시순서")
    private Integer displayOrder;

    @Schema(description = "쇼핑몰_활성화")
    private Boolean visible;

    @Schema(description = "메뉴_아이콘")
    private String iconUrl;

    @Schema(description = "쇼핑몰_라우팅경로")
    private String routePath;
}
