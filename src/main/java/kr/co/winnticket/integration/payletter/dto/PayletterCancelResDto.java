package kr.co.winnticket.integration.payletter.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(title = "[Payletter 결제취소 응답 DTO] payletterCancelResDto")
public class PayletterCancelResDto {

    @NotNull
    @Schema(description = "결제 고유 번호")
    private String tid;

    @NotNull
    @Schema(description = "승인번호")
    private String cid;

    @NotNull
    @Schema(description = "승인금액")
    private Integer amount;

    @NotNull
    @Schema(description = "취소일시(YYYY-MM-DD HH:MM:SS")
    private String cancelDate;

    @Schema(description = "에러 코드")
    private Integer code;

    @Schema(description = "에러 메세지")
    private String message;

    public boolean isSuccess() {
        return code != null && code == 0;
    }
}
