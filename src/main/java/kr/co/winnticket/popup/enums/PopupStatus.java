package kr.co.winnticket.popup.enums;

public enum PopupStatus {
    INACTIVE,   // 비활성 (visible = false)
    SCHEDULED,  // 예정 (startDate > now)
    ACTIVE,     // 노출 중
    EXPIRED     // 종료 (endDate < now)
}