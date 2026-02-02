package kr.co.winnticket.partners.partnerproduct.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.partners.partnerproduct.dto.PartnerProductListResDto;
import kr.co.winnticket.partners.partnerproduct.service.PartnerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/partners")
@Tag(name = "파트너 적용 상품 관리", description = "파트너 적용 상품 목록을 확인할 수 있습니다.")
public class PartnerProductController {

    private final PartnerProductService service;

    @Operation(
            summary = "파트너 적용 상품 + 옵션 조회", description = "파트너의 상품 및 옵션별 판매 현황을 조회합니다.")
    @GetMapping("/{partnerId}/products")
    public List<PartnerProductListResDto> getPartnerProducts(
            @Parameter(description = "파트너 ID", required = true)
            @PathVariable UUID partnerId
    ) {
        return service.getPartnerProducts(partnerId);
    }
}
