package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[Payletter 결제내역 조회 응답 DTO] PayletterTransactionListResDto")
public class PayletterTransactionListResDto {

    @Schema(description = "에러코드")
    private Integer code;

    @Schema(description = "에러 메세지")
    private String message;

    @Schema(description = "전체 조회건수")
    @JsonProperty("total_count")
    private Integer totalCount;

    @Schema(description = "거래 목록")
    @JsonProperty("list")
    private List<PayletterTransactionItemDto> list;

    /** 응답이 성공인지 판단 */
    public boolean isSuccess() {
        return list != null; // Payletter는 성공이면 list 내려줌
    }



}
