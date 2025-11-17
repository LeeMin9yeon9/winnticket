package kr.co.winnticket.community.common.service;

import kr.co.winnticket.community.common.mapper.CommunityCommonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommunityCommonService {
    private final CommunityCommonMapper mapper;

    public void increaseViewCount(UUID auId) {
        mapper.increaseViewCount(auId);
    }

    public void updateIsActive(UUID auId, boolean abIsActive) {
        mapper.updateIsActive(auId, abIsActive);
    }
}
