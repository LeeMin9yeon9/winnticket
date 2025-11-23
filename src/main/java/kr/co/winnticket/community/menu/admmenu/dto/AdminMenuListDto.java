package kr.co.winnticket.community.menu.admmenu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(title =  " [쇼핑몰 메뉴 관리 > 관리자 메뉴 리스트] AdminMenuListDto")
public class AdminMenuListDto {
    private UUID id;

    @Schema(description = "관리자메뉴명_한글")
    private String title;

    @Schema(description = "관리자메뉴명_영문")
    private String titleEn;

    @Schema(description = "관리자메뉴_아이콘")
    private String icon;

    @Schema(description = "관리자메뉴_페이지")
    private String page;

    @Schema(description = "관리자메뉴_표시순서")
    private Integer displayOrder;

    @Schema(description = "관리자메뉴_활성화")
    private Boolean visible;
}
