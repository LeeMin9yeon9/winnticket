package kr.co.winnticket.menu.menu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MenuUpdateOrderDto {
    private UUID id;
    private UUID parentId;
    private Integer displayOrder;
}
