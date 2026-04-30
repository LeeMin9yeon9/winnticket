package kr.co.winnticket.guide.service;

import kr.co.winnticket.guide.dto.GuideStatusDto;
import kr.co.winnticket.guide.mapper.GuideMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuideService {
    private final GuideMapper mapper;

    public Map<String, Boolean> getAll(String accountId) {
        try {
            return mapper.selectAll(accountId).stream()
                    .collect(Collectors.toMap(GuideStatusDto::getMenuKey, GuideStatusDto::isSeen));
        } catch (Exception e) {
            log.warn("[Guide] admin_guide_status 테이블 조회 실패 (마이그레이션 필요): {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public void updateSeen(String accountId, String menuKey, boolean seen) {
        try {
            mapper.upsertSeen(accountId, menuKey, seen);
        } catch (Exception e) {
            log.warn("[Guide] admin_guide_status 업데이트 실패 (마이그레이션 필요): {}", e.getMessage());
        }
    }
}
