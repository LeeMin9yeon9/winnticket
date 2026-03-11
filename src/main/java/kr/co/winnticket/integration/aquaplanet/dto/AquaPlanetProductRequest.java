package kr.co.winnticket.integration.aquaplanet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AquaPlanetProductRequest {

    @Schema(example = "4000")
    private String corpCd;

    @Schema(example = "11900078")
    private String contNo;

    @Schema(example = "20260311")
    private String stdrDate;

}