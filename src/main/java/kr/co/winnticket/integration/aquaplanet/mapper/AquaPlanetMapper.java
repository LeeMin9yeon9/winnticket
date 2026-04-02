package kr.co.winnticket.integration.aquaplanet.mapper;

import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCancelRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetIssueRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRecallRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface AquaPlanetMapper {

    List<AquaPlanetIssueRequest> selectIssueTargets(@Param("orderId") UUID orderId);

    List<AquaPlanetRecallRequest> selectCancelTargets(@Param("orderId") UUID orderId);

    void updateAquaPlanetTicket(
            @Param("ticketId") UUID ticketId,
            @Param("reprCponIndictNo") String reprCponIndictNo,
            @Param("reprCponSeq") String reprCponSeq
    );

    // 쿠폰 사용 처리
    int updateAquaPlanetTicketUsed(
            @Param("reprCponIndictNo") String reprCponIndictNo,
            @Param("usedDate") String usedDate
    );

    // 쿠폰 사용 취소 처리
    int updateAquaPlanetTicketUnused(
            @Param("reprCponIndictNo") String reprCponIndictNo
    );
}