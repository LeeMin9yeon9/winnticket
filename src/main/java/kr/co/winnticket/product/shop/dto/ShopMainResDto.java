package kr.co.winnticket.product.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ShopMainResDto {
    private List<ProductSectionListGetResDto> sections;
    private List<ProductShopListGetResDto> products;
}
