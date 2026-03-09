package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[LS컴퍼니 시설정보 조회 응답 DTO]LsPlaceResDto")
public class LsPlaceResDto {

        @Schema(description = "결과상태")
        private String status;

        @Schema(description = "결과코드")
        private String resultCode;

        @Schema(description = "결과 메시지")
        private String resultMessage;

        @Schema(description = "시설 리스트")
        private List<Place> list;

        @Data
        public static class Place {

            @Schema(description = "시설명")
            private String name;

            @Schema(description = "시설 표시명")
            private String nickName;

            @Schema(description = "시설 코드")
            private String code;

            @Schema(description = "사업자번호")
            private String bizNo;

            @Schema(description = "전화번호")
            private String hp;

            @Schema(description = "주소")
            private String address;

            @Schema(description = "상세주소")
            private String addressDtl;

            @Schema(description = "시설구분")
            private String placeType;

            @Schema(description = "시설테마")
            private String theme;

            @Schema(description = "지역명")
            private String areaName;
        }
    }


