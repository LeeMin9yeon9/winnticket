package kr.co.winnticket.community.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface CommunityCommonMapper {

    // 이벤트 조회수 카운트
    void increaseViewCount(
            @Param("id") UUID auId
    );

    // 이벤트 활성화여부 수정
    void updateIsActive(
            @Param("id") UUID auId,
            @Param("isActive") boolean abIsActive
    );
}
