package kr.co.winnticket.popup.dto;

import kr.co.winnticket.popup.enums.PopupShowCondition;
import kr.co.winnticket.popup.enums.PopupType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PopupCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String title;

    private String contentHtml;
    private String imageUrl;

    @NotNull
    private PopupType type;

    @NotNull
    private PopupShowCondition showCondition = PopupShowCondition.ALWAYS;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private Boolean visible = true;

    private Integer width;
    private Integer height;
    private Integer positionTop;
    private Integer positionLeft;

    private List<String> channelIds;
    private List<String> pagePatterns;
}