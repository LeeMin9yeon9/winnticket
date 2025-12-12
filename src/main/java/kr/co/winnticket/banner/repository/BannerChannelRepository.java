package kr.co.winnticket.banner.repository;


import kr.co.winnticket.banner.entity.BannerChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerChannelRepository extends JpaRepository<BannerChannel, Long> {

    void deleteByBannerId(String bannerId);
}
