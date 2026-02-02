package kr.co.winnticket.partners.partnerproduct.mapper;

import kr.co.winnticket.partners.partnerproduct.dto.PartnerProductListResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PartnerProductMapper {

    // 파트너상품 목록 조회
    List<PartnerProductListResDto> selectPartnerProductList(
            @Param("partnerId") UUID partnerId
    );
}
