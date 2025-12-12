package kr.co.winnticket.banner.controller;


import kr.co.winnticket.banner.dto.*;
import kr.co.winnticket.banner.service.BannerService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ApiResponse<Page<BannerDto>> getBanners(
            @ModelAttribute BannerFilter filter,
            Pageable pageable
    ) {
        return bannerService.getBanners(filter, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ApiResponse<BannerDto> getBanner(@PathVariable String id) {
        return bannerService.getBanner(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerDto> createBanner(
            @RequestBody BannerCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        return bannerService.createBanner(dto, userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerDto> updateBanner(
            @PathVariable String id,
            @RequestBody BannerUpdateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        return bannerService.updateBanner(id, dto, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteBanner(@PathVariable String id) {
        return bannerService.deleteBanner(id);
    }
}
