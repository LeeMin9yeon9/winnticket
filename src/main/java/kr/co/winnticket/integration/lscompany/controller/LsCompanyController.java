package kr.co.winnticket.integration.lscompany.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceResDto;
import kr.co.winnticket.integration.lscompany.service.LsCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lscompany")
public class LsCompanyController {
    private final LsCompanyService service;

    @Operation(summary = "LS컴퍼니 시설정보 조회", description = "LS컴퍼니 시설정보 조회 API")
    @PostMapping("/places")
    public ApiResponse<LsPlaceResDto> getPlaces(){

        return ApiResponse.success(service.getPlaces());
    }
}
