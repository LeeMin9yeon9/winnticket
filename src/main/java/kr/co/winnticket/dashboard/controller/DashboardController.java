package kr.co.winnticket.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.dashboard.dto.DashboardResDto;
import kr.co.winnticket.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 대시보드", description = "관리자 메인 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    @Operation(summary = "대시보드 조회", description = "관리자 메인 화면에 필요한 통계 데이터를 조회한다.")
    public ApiResponse<DashboardResDto> getDashboard(

            @Parameter(description = "조회 기간", schema = @Schema(allowableValues = {"week", "month", "year"}, defaultValue = "week"))
            @RequestParam(defaultValue = "week") String period) {
                DashboardResDto res = service.getDashboard(period);

        return ApiResponse.success(res);
    }
}
