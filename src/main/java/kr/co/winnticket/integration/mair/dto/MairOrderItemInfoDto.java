package kr.co.winnticket.integration.mair.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
// 주문아이템 + 상품코드 DTO
public class MairOrderItemInfoDto {
    private UUID orderItemId;
    private UUID productId;
    private String productCode;   // 상품코드 (ITCD)
    private Integer quantity;     // 수량
}
