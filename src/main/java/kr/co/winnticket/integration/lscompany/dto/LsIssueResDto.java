package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[LS컴퍼니 티켓 발권 응답 DTO] LsIssueResDto")
public class LsIssueResDto {

    @Schema(description = "결과상태")
    private String status;

    @Schema(description = "결과코드")
    private String resultCode;

    @Schema(description = "결과메세지")
    private String resultMessage;

    @Schema(description = "윈앤티켓 주문번호")
    private String orderNo;

    @Schema(description = "대표 바코드")
    private String barcode;

    private List<BarcodeArr> barcodeArr;

    @Data
    public static class BarcodeArr {

        @Schema(description = "바코드번호")
        private String barcode;

        @Schema(description = "윈앤티켓 티켓번호")
        private String transactionId;
    }
}
