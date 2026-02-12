package kr.co.winnticket.banner.service;

import kr.co.winnticket.banner.dto.BannerCreateDto;
import kr.co.winnticket.banner.dto.BannerDto;
import kr.co.winnticket.banner.dto.BannerFilter;
import kr.co.winnticket.banner.dto.BannerUpdateDto;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.enums.BannerStatus;
import kr.co.winnticket.banner.mapper.BannerMapper;
import kr.co.winnticket.banner.mapper.BannerStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerMapper bannerMapper;
    private final BannerStatsMapper statsMapper;

    // ADMIN
    @Transactional
    public void create(BannerCreateDto dto) {
        bannerMapper.insertBanner(dto);
    }

    @Transactional
    public void update(String id, BannerUpdateDto dto) {
        dto.setId(id);
        bannerMapper.updateBanner(dto);
    }

    @Transactional
    public void changeVisible(String id, Boolean visible) {
        bannerMapper.updateVisible(id, visible);
    }

    @Transactional
    public void delete(String id) {
        bannerMapper.softDelete(id);
    }

    @Transactional(readOnly = true)
    public BannerDto getAdminDetail(String id) {
        BannerDto b = bannerMapper.selectBannerById(id);
        if (b == null) return null;
        enrich(b);
        return b;
    }

    @Transactional(readOnly = true)
    public List<BannerDto> getAdminList(BannerFilter filter) {

        List<BannerDto> list = bannerMapper.selectAdminList(filter);

        list.forEach(this::enrich);

        return list;
    }


    // SHOP
    @Transactional(readOnly = true)
    public List<BannerDto> getBannersByPosition(BannerPosition position) {
        List<BannerDto> list = bannerMapper.selectByPosition(position.name());
        list.forEach(this::enrich);
        return list;
    }

    @Transactional
    public void click(String bannerId) {
        statsMapper.increaseClickCount(bannerId);
    }

    @Transactional(readOnly = true)
    public Long getClickCount(String bannerId) {
        return statsMapper.getTotalClickCount(bannerId);
    }

    private void enrich(BannerDto b) {
        b.setStatus(calculateStatus(b));
        b.setClickCount(statsMapper.getTotalClickCount(b.getId()));
    }

    private BannerStatus calculateStatus(BannerDto b) {

        LocalDateTime now = LocalDateTime.now();

        if (!Boolean.TRUE.equals(b.getVisible())) {
            return BannerStatus.INACTIVE;
        }

        if (b.getStartDate() != null && now.isBefore(b.getStartDate())) {
            return BannerStatus.SCHEDULED;
        }

        if (b.getEndDate() != null && now.isAfter(b.getEndDate())) {
            return BannerStatus.EXPIRED;
        }

        return BannerStatus.ACTIVE;


    }
}
