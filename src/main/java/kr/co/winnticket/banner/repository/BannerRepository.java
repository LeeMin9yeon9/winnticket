package kr.co.winnticket.banner.repository;



import kr.co.winnticket.banner.entity.Banner;
import kr.co.winnticket.banner.enums.BannerPosition;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String>, JpaSpecificationExecutor<Banner> {

    @Query("""
        SELECT b FROM Banner b
        LEFT JOIN FETCH b.channels bc
        WHERE b.position = :position
          AND b.visible = true
          AND b.startDate <= :now
          AND b.endDate >= :now
        ORDER BY b.displayOrder ASC, b.createdAt ASC
        """)
    List<Banner> findActiveBannersByPosition(
            @Param("position") BannerPosition position,
            @Param("now") LocalDateTime now
    );

    @Query("""
        SELECT DISTINCT b FROM Banner b
        LEFT JOIN b.channels bc
        WHERE b.position = :position
          AND b.visible = true
          AND b.startDate <= :now
          AND b.endDate >= :now
          AND (bc.channelId = :channelId OR SIZE(b.channels) = 0)
        ORDER BY b.displayOrder ASC, b.createdAt ASC
        """)
    List<Banner> findActiveBannersByPositionAndChannel(
            @Param("position") BannerPosition position,
            @Param("channelId") String channelId,
            @Param("now") LocalDateTime now
    );

    Page<Banner> findByNameContainingOrDescriptionContaining(
            String name,
            String description,
            Pageable pageable
    );
}
