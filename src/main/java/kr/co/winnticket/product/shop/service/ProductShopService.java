package kr.co.winnticket.product.shop.service;

import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kr.co.winnticket.product.shop.mapper.ProductShopMapper;

@Service
@RequiredArgsConstructor
public class ProductShopService {
    private final ProductShopMapper mapper;

    // 상품 목록 조회
    public List<ProductShopListGetResDto> selectProductList(String name, UUID categoryId) {
        List<ProductShopListGetResDto> products = mapper.selectProductList(name, categoryId);

        return products.stream()
            .peek(p -> {
                int discountRate = (int) Math.floor(((p.getPrice() - p.getDiscountPrice()) / (double) p.getPrice()) * 100);
                p.setDiscountRate(discountRate);
            })
            .collect(Collectors.toList());
    }
}
