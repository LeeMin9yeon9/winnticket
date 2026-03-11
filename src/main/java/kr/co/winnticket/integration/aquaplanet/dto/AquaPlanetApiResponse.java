package kr.co.winnticket.integration.aquaplanet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AquaPlanetApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}