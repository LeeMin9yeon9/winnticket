package kr.co.winnticket.popup.controller;


import  kr.co.winnticket.common.dto.ApiResponse;
import  kr.co.winnticket.popup.dto.*;
import kr.co.winnticket.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ApiResponse<Page<PopupDto>> getPopups(
            @ModelAttribute PopupFilter filter,
            Pageable pageable
    ) {
        return popupService.getPopups(filter, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ApiResponse<PopupDto> getPopup(@PathVariable String id) {
        return popupService.getPopup(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PopupDto> createPopup(
            @RequestBody PopupCreateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        return popupService.createPopup(dto, userId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PopupDto> updatePopup(
            @PathVariable String id,
            @RequestBody PopupUpdateDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        return popupService.updatePopup(id, dto, userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deletePopup(@PathVariable String id) {
        return popupService.deletePopup(id);
    }
}
