package kr.co.winnticket.guide.mapper;

import kr.co.winnticket.guide.dto.GuideStatusDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GuideMapper {
    List<GuideStatusDto> selectAll(@Param("accountId") String accountId);
    void upsertSeen(@Param("accountId") String accountId, @Param("menuKey") String menuKey, @Param("seen") boolean seen);
}
