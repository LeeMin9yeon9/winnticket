package kr.co.winnticket.menu.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class MenuSearchDto{
    @Schema(description = "메뉴_ID")
    private UUID id;
    @Schema(description = "메뉴_이름")
    private String name;
    @Schema(description = "메뉴_코드")
    private String code;
    @Schema(description = "메뉴_래벨")
    private Integer level;
    @Schema(description = "메뉴_표시순서")
    private Integer displayOrder;
    @Schema(description = "메뉴_활성화_여부")
    private Boolean visible;


}
