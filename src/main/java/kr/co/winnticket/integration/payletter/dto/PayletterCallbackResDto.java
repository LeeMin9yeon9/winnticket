package kr.co.winnticket.integration.payletter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "[Payletter callback 응답 DTO] PayletterCallbackResDto")
public class PayletterCallbackResDto {
    @Schema(description = "성공=0", example = "0")
    private Integer code;

    @Schema(description = "처리 결과 메시지", example = "OK")
    private String message;

    public boolean isSuccess() {
        return code != null && code ==0;
    }
}
