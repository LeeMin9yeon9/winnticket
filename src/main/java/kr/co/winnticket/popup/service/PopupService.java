package kr.co.winnticket.popup.service;



import  kr.co.winnticket.common.dto.ApiResponse;
import  kr.co.winnticket.popup.dto.*;
import  kr.co.winnticket.popup.entity.*;
import  kr.co.winnticket.popup.enums.PopupShowCondition;
import  kr.co.winnticket.popup.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;
    private final PopupChannelRepository popupChannelRepository;
    private final PopupPageRepository popupPageRepository;
    private final PopupUserPreferenceRepository popupUserPreferenceRepository;

    // ===== 관리자용 목록 조회 =====
    public ApiResponse<Page<PopupDto>> getPopups(PopupFilter filter, Pageable pageable) {
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        Page<Popup> result = popupRepository
                .findByNameContainingOrTitleContaining(keyword, keyword, pageable);

        Page<PopupDto> dtos = result.map(this::convertToDto);
        return ApiResponse.success(dtos);
    }

    public ApiResponse<PopupDto> getPopup(String id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("팝업을 찾을 수 없습니다."));
        return ApiResponse.success(convertToDto(popup));
    }

    // ===== 생성 / 수정 / 삭제 =====
    @Transactional
    public ApiResponse<PopupDto> createPopup(PopupCreateDto dto, String userId) {
        Popup popup = new Popup();
        popup.setName(dto.getName());
        popup.setTitle(dto.getTitle());
        popup.setContentHtml(dto.getContentHtml());
        popup.setImageUrl(dto.getImageUrl());
        popup.setType(dto.getType());
        popup.setShowCondition(dto.getShowCondition());
        popup.setStartDate(dto.getStartDate());
        popup.setEndDate(dto.getEndDate());
        popup.setVisible(dto.getVisible());
        popup.setWidth(dto.getWidth());
        popup.setHeight(dto.getHeight());
        popup.setPositionTop(dto.getPositionTop());
        popup.setPositionLeft(dto.getPositionLeft());

        popup = popupRepository.save(popup);

        // 채널 매핑
        if (dto.getChannelIds() != null) {
            for (String channelId : dto.getChannelIds()) {
                PopupChannel ch = new PopupChannel();
                ch.setPopup(popup);
                ch.setChannelId(channelId);
                popupChannelRepository.save(ch);
            }
        }

        // 페이지 매핑
        if (dto.getPagePatterns() != null) {
            for (String path : dto.getPagePatterns()) {
                PopupPage pg = new PopupPage();
                pg.setPopup(popup);
                pg.setPathPattern(path);
                popupPageRepository.save(pg);
            }
        }

        return ApiResponse.success("팝업이 생성되었습니다.",convertToDto(popup));
    }

    @Transactional
    public ApiResponse<PopupDto> updatePopup(String id, PopupUpdateDto dto, String userId) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("팝업을 찾을 수 없습니다."));

        if (dto.getName() != null) popup.setName(dto.getName());
        if (dto.getTitle() != null) popup.setTitle(dto.getTitle());
        if (dto.getContentHtml() != null) popup.setContentHtml(dto.getContentHtml());
        if (dto.getImageUrl() != null) popup.setImageUrl(dto.getImageUrl());
        if (dto.getType() != null) popup.setType(dto.getType());
        if (dto.getShowCondition() != null) popup.setShowCondition(dto.getShowCondition());
        if (dto.getStartDate() != null) popup.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) popup.setEndDate(dto.getEndDate());
        if (dto.getVisible() != null) popup.setVisible(dto.getVisible());
        if (dto.getWidth() != null) popup.setWidth(dto.getWidth());
        if (dto.getHeight() != null) popup.setHeight(dto.getHeight());
        if (dto.getPositionTop() != null) popup.setPositionTop(dto.getPositionTop());
        if (dto.getPositionLeft() != null) popup.setPositionLeft(dto.getPositionLeft());

        // 채널 매핑 갱신
        if (dto.getChannelIds() != null) {
            popupChannelRepository.deleteByPopupId(id);
            for (String channelId : dto.getChannelIds()) {
                PopupChannel ch = new PopupChannel();
                ch.setPopup(popup);
                ch.setChannelId(channelId);
                popupChannelRepository.save(ch);
            }
        }

        // 페이지 매핑 갱신
        if (dto.getPagePatterns() != null) {
            popupPageRepository.deleteByPopupId(id);
            for (String path : dto.getPagePatterns()) {
                PopupPage pg = new PopupPage();
                pg.setPopup(popup);
                pg.setPathPattern(path);
                popupPageRepository.save(pg);
            }
        }

        return ApiResponse.success("팝업이 수정되었습니다.",convertToDto(popup));
    }

    @Transactional
    public ApiResponse<Void> deletePopup(String id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("팝업을 찾을 수 없습니다."));
        popupRepository.delete(popup);
        return ApiResponse.success( "팝업이 삭제되었습니다.",null);
    }

    // ===== 쇼핑몰용: 노출 대상 팝업 조회 =====
    public ApiResponse<List<PopupDto>> getShopPopups(
            String channelId,
            String pagePath,
            String userId,
            String sessionId
    ) {
        List<Popup> popups = popupRepository.findActivePopupsByChannelAndPage(
                channelId,
                pagePath,
                LocalDateTime.now()
        );

        List<Popup> filtered = popups.stream()
                .filter(p -> shouldShowPopup(p, userId, sessionId))
                .collect(Collectors.toList());

        List<PopupDto> dtos = filtered.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ApiResponse.success(dtos);
    }

    /**
     * 팝업 표시 여부 판단
     */
    private boolean shouldShowPopup(Popup popup, String userId, String sessionId) {
        PopupUserPreference pref = popupUserPreferenceRepository
                .findByPopupIdAndUserOrSession(popup.getId(), userId, sessionId)
                .orElse(null);

        // 영구 "다시 보지 않기"
        if (pref != null && Boolean.TRUE.equals(pref.getNeverShow())) {
            return false;
        }

        // "오늘 하루 보지 않기" 등 유효기간
        if (pref != null && pref.getClosedUntil() != null &&
                LocalDateTime.now().isBefore(pref.getClosedUntil())) {
            return false;
        }

        PopupShowCondition cond = popup.getShowCondition();
        if (cond == null) cond = PopupShowCondition.ALWAYS;

        switch (cond) {
            case ALWAYS:
                return true;

            case FIRST_VISIT:
                // 첫 방문이면 기록이 없는 상태
                return pref == null;

            case ONCE_PER_DAY:
                if (pref == null || pref.getUpdatedAt() == null) return true;
                LocalDate last = pref.getUpdatedAt().toLocalDate();
                return last.isBefore(LocalDate.now());

            case ONCE_PER_SESSION:
                // 실제로는 Redis 등에 세션별 표시 여부 기록 필요
                return !hasShownInSession(popup.getId(), sessionId);

            default:
                return true;
        }
    }

    /**
     * 세션 단위 노출 여부 체크 (임시 구현: 항상 false 반환)
     * 실제 운영에서는 Redis/캐시로 세션별 기록 관리 필요
     */
    private boolean hasShownInSession(String popupId, String sessionId) {
        if (sessionId == null) {
            return false;
        }
        // TODO: Redis 등으로 구현
        return false;
    }

    // ===== 사용자 preference 설정 =====
    @Transactional
    public ApiResponse<Void> setTodayClose(String popupId, String userId, String sessionId) {
        PopupUserPreference pref = popupUserPreferenceRepository
                .findByPopupIdAndUserOrSession(popupId, userId, sessionId)
                .orElseGet(PopupUserPreference::new);

        pref.setPopupId(popupId);
        pref.setUserId(userId);
        pref.setSessionId(sessionId);
        pref.setClosedUntil(LocalDateTime.now().plusDays(1));
        pref.setNeverShow(false);

        popupUserPreferenceRepository.save(pref);

        return ApiResponse.success( "오늘 하루 보지 않기 설정이 완료되었습니다.",null);
    }

    @Transactional
    public ApiResponse<Void> setNeverShow(String popupId, String userId, String sessionId) {
        PopupUserPreference pref = popupUserPreferenceRepository
                .findByPopupIdAndUserOrSession(popupId, userId, sessionId)
                .orElseGet(PopupUserPreference::new);

        pref.setPopupId(popupId);
        pref.setUserId(userId);
        pref.setSessionId(sessionId);
        pref.setNeverShow(true);
        pref.setClosedUntil(null);

        popupUserPreferenceRepository.save(pref);

        return ApiResponse.success("다시 보지 않기 설정이 완료되었습니다.",null);
    }

    @Transactional
    public void incrementViewCount(String popupId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new EntityNotFoundException("팝업을 찾을 수 없습니다."));
        popup.incrementViewCount();
    }

    @Transactional
    public void incrementClickCount(String popupId) {
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new EntityNotFoundException("팝업을 찾을 수 없습니다."));
        popup.incrementClickCount();
    }

    // ===== private util =====
    private PopupDto convertToDto(Popup popup) {
        PopupDto dto = new PopupDto();
        dto.setId(popup.getId());
        dto.setName(popup.getName());
        dto.setTitle(popup.getTitle());
        dto.setContentHtml(popup.getContentHtml());
        dto.setImageUrl(popup.getImageUrl());
        dto.setType(popup.getType());
        dto.setShowCondition(popup.getShowCondition());
        dto.setStartDate(popup.getStartDate());
        dto.setEndDate(popup.getEndDate());
        dto.setVisible(popup.getVisible());
        dto.setWidth(popup.getWidth());
        dto.setHeight(popup.getHeight());
        dto.setPositionTop(popup.getPositionTop());
        dto.setPositionLeft(popup.getPositionLeft());
        dto.setViewCount(popup.getViewCount());
        dto.setClickCount(popup.getClickCount());
        dto.setStatus(popup.getStatus());
        dto.setCreatedAt(popup.getCreatedAt());
        dto.setUpdatedAt(popup.getUpdatedAt());

        dto.setChannelIds(
                popup.getChannels().stream()
                        .map(PopupChannel::getChannelId)
                        .collect(Collectors.toList())
        );

        dto.setPagePatterns(
                popup.getPages().stream()
                        .map(PopupPage::getPathPattern)
                        .collect(Collectors.toList())
        );

        return dto;
    }
}
