package kr.co.winnticket.popup.repository;


import kr.co.winnticket.popup.entity.PopupUserPreference;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupUserPreferenceRepository extends JpaRepository<PopupUserPreference, Long> {

    Optional<PopupUserPreference> findByPopupIdAndUserId(String popupId, String userId);

    Optional<PopupUserPreference> findByPopupIdAndSessionId(String popupId, String sessionId);

    // 편의 메서드: userId 우선, 없으면 session 기준
    default Optional<PopupUserPreference> findByPopupIdAndUserOrSession(
            String popupId,
            String userId,
            String sessionId
    ) {
        if (userId != null) {
            return findByPopupIdAndUserId(popupId, userId);
        } else if (sessionId != null) {
            return findByPopupIdAndSessionId(popupId, sessionId);
        } else {
            return Optional.empty();
        }
    }
}