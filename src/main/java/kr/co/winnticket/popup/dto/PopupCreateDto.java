package kr.co.winnticket.popup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopupCreateDto {

    @NotBlank
    private String name;

    private String title;

    private String contentHtml;
    private String imageUrl;

    @NotNull
    private PopupType type;

    private PopupShowCondition showCondition = PopupShowCondition.ALWAYS;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Boolean visible = true;

    private Integer width;
    private Integer height;
    private Integer positionTop;
    private Integer positionLeft;
    private Integer displayOrder = 0;

    private String linkUrl;
    private String linkTarget;

    private List<String> channelIds;
    private List<String> pagePatterns;
}