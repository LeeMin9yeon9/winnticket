package kr.co.winnticket.popup.dto;

import kr.co.winnticket.popup.enums.PopupShowCondition;
import kr.co.winnticket.popup.enums.PopupType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupUpdateDto {

    private String name;
    private String title;
    private String contentHtml;
    private String imageUrl;
    private PopupType type;
    private PopupShowCondition showCondition;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean visible;

    private Integer width;
    private Integer height;
    private Integer positionTop;
    private Integer positionLeft;

    private List<String> channelIds;
    private List<String> pagePatterns;
}