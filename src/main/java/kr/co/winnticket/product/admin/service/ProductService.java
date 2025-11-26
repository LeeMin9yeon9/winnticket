package kr.co.winnticket.product.admin.service;

import kr.co.winnticket.product.admin.dto.ProductPostReqDto;
import kr.co.winnticket.product.admin.dto.ProductListGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper mapper;

    public List<ProductListGetResDto> selectProductList(String srchWord, UUID categoryId, String salesStatus) {
        List<ProductListGetResDto> lModel = mapper.selectProductList(srchWord, categoryId, salesStatus);

        return lModel;
    }

    public void insertProduct(ProductPostReqDto model) {
        mapper.insertProduct(model);
    }
}
