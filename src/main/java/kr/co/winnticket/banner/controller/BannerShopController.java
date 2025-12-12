package kr.co.winnticket.banner.controller;



import kr.co.winnticket.banner.dto.BannerDto;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.service.BannerService;
import kr.co.winnticket.common.dto.ApiResponse;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/banners")
@RequiredArgsConstructor
public class BannerShopController {

    private final BannerService bannerService;

    @GetMapping
    public ApiResponse<List<BannerDto>> getBanners(
            @RequestParam BannerPosition position,
            @RequestParam(required = false) UUID channelId
    ) {
        return bannerService.getShopBanners(position, channelId);
    }

    @PostMapping("/{id}/view")
    public ApiResponse<Void> logView(@PathVariable String id) {
        bannerService.incrementViewCount(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/click")
    public ApiResponse<Void> logClick(@PathVariable String id) {
        bannerService.incrementClickCount(id);
        return ApiResponse.success(null);
    }
}
