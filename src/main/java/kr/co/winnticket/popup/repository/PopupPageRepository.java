package kr.co.winnticket.popup.repository;

import kr.co.winnticket.popup.entity.PopupPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupPageRepository extends JpaRepository<PopupPage, Long> {

    void deleteByPopupId(String popupId);
}