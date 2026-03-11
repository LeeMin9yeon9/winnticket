package kr.co.winnticket.integration.aquaplanet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AquaPlanetIssueRequest {

    @Schema(example = "4000")
    private String corpCd;

    @Schema(example = "11900078")
    private String contNo;

    @Schema(example = "1")
    private Integer seq;

    @Schema(example = "20260311")
    private String issueDate;

    @Schema(example = "U133192")
    private String goodsNo;

    @Schema(example = "1")
    private Integer issueQty;

    @Schema(example = "N")
    private String unityIssueYn;

    @Schema(example = "김한빛")
    private String rcverNm;

    @Schema(example = "82")
    private String rcverTelNationNo;

    @Schema(example = "010")
    private String rcverTelAreaNo;

    @Schema(example = "9930")
    private String rcverTelExchgeNo;

    @Schema(example = "5681")
    private String rcverTelNo;

}