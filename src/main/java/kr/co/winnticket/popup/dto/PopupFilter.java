package kr.co.winnticket.popup.dto;

import kr.co.winnticket.popup.enums.PopupStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupFilter {

    private String keyword;   // name/title 검색
    private PopupStatus status;
    private Boolean visible;
}