package kr.co.winnticket.menu.menu.entity;

import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategory {

    private UUID id;
    private String name;
    private String code;

    private Integer level;
    private UUID parentId;
    private Integer displayOrder; // 표시순서
    private Boolean visible;
    private String routePath;



}
