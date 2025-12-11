package kr.co.winnticket.banner.service;


import kr.co.winnticket.banner.dto.*;
import kr.co.winnticket.banner.entity.Banner;
import kr.co.winnticket.banner.entity.BannerChannel;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.repository.BannerChannelRepository;
import kr.co.winnticket.banner.repository.BannerRepository;
import kr.co.winnticket.common.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
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
public class BannerService {

    private final BannerRepository bannerRepository;
    private final BannerChannelRepository bannerChannelRepository;

    /**
     * 관리자용 배너 목록 조회 (필터는 최소 구현)
     */
    public ApiResponse<Page<BannerDto>> getBanners(BannerFilter filter, Pageable pageable) {
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        Page<Banner> result = bannerRepository
                .findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);

        Page<BannerDto> dtos = result.map(this::convertToDto);
        return ApiResponse.success(dtos);
    }

    public ApiResponse<BannerDto> getBanner(String id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));

        return ApiResponse.success(convertToDto(banner));
    }

    @Transactional
    public ApiResponse<BannerDto> createBanner(BannerCreateDto dto, String userId) {
        Banner banner = new Banner();
        banner.setName(dto.getName());
        banner.setDescription(dto.getDescription());
        banner.setType(dto.getType());
        banner.setPosition(dto.getPosition());
        banner.setImageUrl(dto.getImageUrl());
        banner.setImageUrlMobile(dto.getImageUrlMobile());
        banner.setHtmlContent(dto.getHtmlContent());
        banner.setVideoUrl(dto.getVideoUrl());
        banner.setClickAction(dto.getClickAction());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setLinkTarget(dto.getLinkTarget());
        banner.setStartDate(dto.getStartDate());
        banner.setEndDate(dto.getEndDate());
        banner.setVisible(dto.getVisible());
        banner.setDisplayOrder(dto.getDisplayOrder());
        banner.setWidth(dto.getWidth());
        banner.setHeight(dto.getHeight());
        banner.setMobileWidth(dto.getMobileWidth());
        banner.setMobileHeight(dto.getMobileHeight());

        banner = bannerRepository.save(banner);

        if (dto.getChannelIds() != null) {
            for (String channelId : dto.getChannelIds()) {
                BannerChannel bc = new BannerChannel();
                bc.setBanner(banner);
                bc.setChannelId(channelId);
                bannerChannelRepository.save(bc);
            }
        }

        return ApiResponse.success("배너가 생성되었습니다.",convertToDto(banner));
    }

    @Transactional
    public ApiResponse<BannerDto> updateBanner(String id, BannerUpdateDto dto, String userId) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));

        if (dto.getName() != null) banner.setName(dto.getName());
        if (dto.getDescription() != null) banner.setDescription(dto.getDescription());
        if (dto.getType() != null) banner.setType(dto.getType());
        if (dto.getPosition() != null) banner.setPosition(dto.getPosition());
        if (dto.getImageUrl() != null) banner.setImageUrl(dto.getImageUrl());
        if (dto.getImageUrlMobile() != null) banner.setImageUrlMobile(dto.getImageUrlMobile());
        if (dto.getHtmlContent() != null) banner.setHtmlContent(dto.getHtmlContent());
        if (dto.getVideoUrl() != null) banner.setVideoUrl(dto.getVideoUrl());
        if (dto.getClickAction() != null) banner.setClickAction(dto.getClickAction());
        if (dto.getLinkUrl() != null) banner.setLinkUrl(dto.getLinkUrl());
        if (dto.getLinkTarget() != null) banner.setLinkTarget(dto.getLinkTarget());
        if (dto.getStartDate() != null) banner.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) banner.setEndDate(dto.getEndDate());
        if (dto.getVisible() != null) banner.setVisible(dto.getVisible());
        if (dto.getDisplayOrder() != null) banner.setDisplayOrder(dto.getDisplayOrder());
        if (dto.getWidth() != null) banner.setWidth(dto.getWidth());
        if (dto.getHeight() != null) banner.setHeight(dto.getHeight());
        if (dto.getMobileWidth() != null) banner.setMobileWidth(dto.getMobileWidth());
        if (dto.getMobileHeight() != null) banner.setMobileHeight(dto.getMobileHeight());

        if (dto.getChannelIds() != null) {
            bannerChannelRepository.deleteByBannerId(id);
            for (String channelId : dto.getChannelIds()) {
                BannerChannel bc = new BannerChannel();
                bc.setBanner(banner);
                bc.setChannelId(channelId);
                bannerChannelRepository.save(bc);
            }
        }

        return ApiResponse.success("배너가 수정되었습니다.", convertToDto(banner));

    }

    @Transactional
    public ApiResponse<Void> deleteBanner(String id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
        bannerRepository.delete(banner);
        return ApiResponse.success("배너가 삭제되었습니다.", null);
    }

    public ApiResponse<List<BannerDto>> getShopBanners(BannerPosition position, String channelId) {
        List<Banner> banners;
        LocalDateTime now = LocalDateTime.now();

        if (channelId != null && !channelId.isBlank()) {
            banners = bannerRepository.findActiveBannersByPositionAndChannel(position, channelId, now);
        } else {
            banners = bannerRepository.findActiveBannersByPosition(position, now);
        }

        List<BannerDto> dtos = banners.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ApiResponse.success(dtos);
    }

    @Transactional
    public void incrementViewCount(String bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
        banner.incrementViewCount();
    }

    @Transactional
    public void incrementClickCount(String bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new EntityNotFoundException("배너를 찾을 수 없습니다."));
        banner.incrementClickCount();
    }

    // ===== private util =====
    private BannerDto convertToDto(Banner banner) {
        BannerDto dto = new BannerDto();
        dto.setId(banner.getId());
        dto.setName(banner.getName());
        dto.setDescription(banner.getDescription());
        dto.setType(banner.getType());
        dto.setPosition(banner.getPosition());
        dto.setImageUrl(banner.getImageUrl());
        dto.setImageUrlMobile(banner.getImageUrlMobile());
        dto.setHtmlContent(banner.getHtmlContent());
        dto.setVideoUrl(banner.getVideoUrl());
        dto.setClickAction(banner.getClickAction());
        dto.setLinkUrl(banner.getLinkUrl());
        dto.setLinkTarget(banner.getLinkTarget());
        dto.setStartDate(banner.getStartDate());
        dto.setEndDate(banner.getEndDate());
        dto.setVisible(banner.getVisible());
        dto.setDisplayOrder(banner.getDisplayOrder());
        dto.setViewCount(banner.getViewCount());
        dto.setClickCount(banner.getClickCount());
        dto.setWidth(banner.getWidth());
        dto.setHeight(banner.getHeight());
        dto.setMobileWidth(banner.getMobileWidth());
        dto.setMobileHeight(banner.getMobileHeight());
        dto.setStatus(banner.getStatus());
        dto.setCreatedAt(banner.getCreatedAt());
        dto.setUpdatedAt(banner.getUpdatedAt());
        dto.setChannelIds(
                banner.getChannels().stream()
                        .map(BannerChannel::getChannelId)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
