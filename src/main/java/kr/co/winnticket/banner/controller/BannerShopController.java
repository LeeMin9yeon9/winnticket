package kr.co.winnticket.banner.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.banner.dto.BannerDto;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.service.BannerService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SHOP 배너", description = "쇼핑몰 배너 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/banners")
public class BannerShopController {

    private final BannerService service;

    @GetMapping
    @Operation(summary = "위치별 배너 조회", description = "position별로 현재 노출 가능한 배너 목록 조회")
    public ResponseEntity<ApiResponse<List<BannerDto>>> getBanners(
            @Parameter(description = "배너 위치", example = "MAIN_TOP")
            @RequestParam BannerPosition position
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.getBannersByPosition(position))
        );
    }

    @PostMapping("/{id}/click")
    @Operation(summary = "배너 클릭 처리", description = "내부 페이지 이동 전에 호출하여 클릭 카운트를 증가")
    public ResponseEntity<ApiResponse<Void>> click(
            @Parameter(description = "배너 ID")
            @PathVariable String id
    ) {
        service.click(id);
        return ResponseEntity.ok(ApiResponse.success("클릭 처리 완료", null));
    }
}
