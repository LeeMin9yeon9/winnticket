package kr.co.winnticket.integration.lscompany.dto;

import lombok.Data;

import java.util.UUID;

@Data
// 주문아이템 dto
public class LsOrderItemInfoDto {

    private UUID orderItemId;

    private UUID productId;

    private String optionId;   // LS optionId

    private String classify;      // 권종명

    private String optionType;

    private String optionTypeCode;

    private Integer quantity;  // 주문수량

    private Integer price;     // 판매가

    private Integer discount;  // 할인금액
}
