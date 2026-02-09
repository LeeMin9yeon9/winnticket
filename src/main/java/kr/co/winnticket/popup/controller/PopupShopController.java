package kr.co.winnticket.popup.controller;


import jakarta.servlet.http.HttpServletRequest;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.popup.dto.PopupDto;
import kr.co.winnticket.popup.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop/popups")
@RequiredArgsConstructor
public class PopupShopController {

    private final PopupService popupService;

    /**
     * 팝업 조회: 채널 + 페이지 + 사용자 설정 고려
     */
    @GetMapping
    public ApiResponse<List<PopupDto>> getPopups(
            @RequestParam(required = false) String channelCode,
            @RequestParam(required = false) String pagePath,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : null;
        String sessionId = request.getSession().getId();

        return popupService.getShopPopups(channelCode, pagePath, userId, sessionId);
    }

    /**
     * 오늘 하루 보지 않기
     */
    @PostMapping("/{id}/close-today")
    public ApiResponse<Void> closeToday(
            @PathVariable("id") String popupId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : null;
        String sessionId = request.getSession().getId();
        return popupService.setTodayClose(popupId, userId, sessionId);
    }

    /**
     * 다시 보지 않기(영구)
     */
    @PostMapping("/{id}/never-show")
    public ApiResponse<Void> neverShow(
            @PathVariable("id") String popupId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request
    ) {
        String userId = userDetails != null ? userDetails.getUsername() : null;
        String sessionId = request.getSession().getId();
        return popupService.setNeverShow(popupId, userId, sessionId);
    }

    /**
     * 뷰/클릭 카운트 기록 (원하면 사용)
     */
    @PostMapping("/{id}/view")
    public ApiResponse<Void> logView(@PathVariable("id") String popupId) {
        popupService.incrementViewCount(popupId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/click")
    public ApiResponse<Void> logClick(@PathVariable("id") String popupId) {
        popupService.incrementClickCount(popupId);
        return ApiResponse.success(null);
    }
}
