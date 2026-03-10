package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[LS컴퍼니 상품 조회 응답 DTO] LsProductResDto")
public class LsProductResDto {

    @Schema(description = "결과상태")
    private String status;

    @Schema(description = "결과코드")
    private String resultCode;

    @Schema(description = "결과 메세지")
    private String resultMessage;

    private List<Product> list;

    @Data
    public static class Product{

        @JsonProperty("product_name")
        @Schema(description = "LS 상품명")
        private String productName;

        @JsonProperty("product_code")
        @Schema(description = "LS 상품코드")
        private String productCode;

        @Schema(description = "문자 바코드 발송 여부")
        private String sendSmsYn;

        private List<Image> images;
        private List<Option> option;

        @Data
        public static class Image{
            @Schema(description = "이미지타입")
            private String imageType;

            @Schema(description = "이미지URI")
            private String imageUri;
        }
        @Data
        public static class Option{
            @Schema(description = "상품 판매가")
            private String salePrice;

            @Schema(description = "권종명")
            private String classify;

            @Schema(description = "권종코드",example = "KJ1:성인, KJ2:청소년, KJ3:소인")
            private String classifyCode;

            @Schema(description = "옵션타입명")
            private String optionType;

            @Schema(description = "옵션타입코드" , example = "PRT:단품, PRT2:패키지")
            private String optionTypeCode;
        }
    }
}
