package kr.co.winnticket.guide.service;

import kr.co.winnticket.guide.dto.GuideStatusDto;
import kr.co.winnticket.guide.mapper.GuideMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final GuideMapper mapper;

    public Map<String, Boolean> getAll(String accountId) {
        return mapper.selectAll(accountId).stream()
                .collect(Collectors.toMap(GuideStatusDto::getMenuKey, GuideStatusDto::isSeen));
    }

    public void updateSeen(String accountId, String menuKey, boolean seen) {
        mapper.upsertSeen(accountId, menuKey, seen);
    }
}
