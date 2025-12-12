package kr.co.winnticket.popup.repository;

import kr.co.winnticket.popup.entity.PopupChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupChannelRepository extends JpaRepository<PopupChannel, Long> {

    void deleteByPopupId(String popupId);
}