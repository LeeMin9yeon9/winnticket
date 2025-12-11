package kr.co.winnticket.popup.enums;


public enum PopupShowCondition {
    ALWAYS,          // 항상 표시
    FIRST_VISIT,     // 첫 방문에만
    ONCE_PER_DAY,    // 하루 1회
    ONCE_PER_SESSION // 세션당 1회 (Redis 등으로 체크)
}