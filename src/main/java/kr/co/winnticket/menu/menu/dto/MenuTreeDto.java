package kr.co.winnticket.menu.menu.dto;

import kr.co.winnticket.menu.menu.entity.MenuCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuTreeDto {
    private UUID id;
    private String name;
    private String code;
    private String routePath;
    private Integer displayOrder;

    private List<MenuTreeDto> childrenTree = new ArrayList<>();

    public MenuTreeDto(MenuCategory menuCategory) {
        this.id = UUID.randomUUID();
        this.name = menuCategory.getName();
        this.code = menuCategory.getCode();
        this.routePath = menuCategory.getRoutePath();
        this.displayOrder = menuCategory.getDisplayOrder();
    }
}
