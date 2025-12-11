package kr.co.winnticket.popup.repository;


import  kr.co.winnticket.popup.entity.Popup;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupRepository extends JpaRepository<Popup, String>, JpaSpecificationExecutor<Popup> {

    /**
     * 채널 + 페이지 + 기간 + visible 기준으로 활성 팝업 조회
     * (페이지 path는 LIKE로 간단히 처리 - 필요하면 나중에 정규식/특수 매칭)
     */
    @Query("""
        SELECT DISTINCT p FROM Popup p
        LEFT JOIN p.channels ch
        LEFT JOIN p.pages pg
        WHERE p.visible = true
          AND p.startDate <= :now
          AND p.endDate >= :now
          AND (:channelId IS NULL OR ch.channelId = :channelId OR SIZE(p.channels) = 0)
          AND (:pagePath IS NULL OR pg.pathPattern IS NULL OR :pagePath LIKE pg.pathPattern)
        ORDER BY p.createdAt ASC
        """)
    List<Popup> findActivePopupsByChannelAndPage(
            @Param("channelId") String channelId,
            @Param("pagePath") String pagePath,
            @Param("now") LocalDateTime now
    );

    Page<Popup> findByNameContainingOrTitleContaining(
            String name,
            String title,
            Pageable pageable
    );
}