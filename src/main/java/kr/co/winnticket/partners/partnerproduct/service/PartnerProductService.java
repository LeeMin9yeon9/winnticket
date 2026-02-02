package kr.co.winnticket.partners.partnerproduct.service;

import kr.co.winnticket.partners.partnerproduct.dto.PartnerProductListResDto;
import kr.co.winnticket.partners.partnerproduct.mapper.PartnerProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerProductService {

    private final PartnerProductMapper mapper;

    // 파트너 상품 목록 조회
    public List<PartnerProductListResDto> getPartnerProducts(UUID partnerId){
        return mapper.selectPartnerProductList(partnerId);
    }
}
