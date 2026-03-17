package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[LS컴퍼니 상품 조회 응답 DTO] LsProductResDto")
public class LsProductResDto {

    @Schema(description = "결과상태")
    private String status;

    @Schema(description = "결과코드")
    private String resultCode;

    private String rescode;

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
        private List<AgentInfo> agent;

        @Data
        public static class Image{
            @Schema(description = "이미지타입")
            private String imageType;

            @Schema(description = "이미지URI")
            private String imageUri;
        }
        @Data
        public static class Option{

            @Schema(description = "옵션 ID")
            private String optionId;

            @Schema(description = "옵션명")
            private String name;

            @Schema(description = "상품 판매 시작일")
            private String startDate;

            @Schema(description = "상품 판매 종료일")
            private String endDate;

            @Schema(description = "유효기간 타입",example = "date(기간지정), day(판매일기준일자), order(지정유효기간), first(최초사용일기준)")
            private String expireType;

            @Schema(description = "유효기간 시작일")
            private String expireStartDate;

            @Schema(description = "유효기간 종료일")
            private String expireEndDate;

            @Schema(description = "유효기간 설정일")
            private String expireDay;

            @Schema(description = "상품 정상가")
            private String normalPrice;

            @Schema(description = "상품 판매가")
            private String salePrice;

            @Schema(description = "권종명")
            private String classify;

            @Schema(description = "권종코드",example = "KJ1:성인, KJ2:청소년, KJ3:소인")
            private String classifyCode;

            @Schema(description = "옵션타입명", example = "단품 , 패키지")
            private String optionType;

            @Schema(description = "옵션타입코드" , example = "PRT:단품, PRT2:패키지")
            private String optionTypeCode;
        }

        @Data
        public static class AgentInfo{

            @Schema(description = "업체코드")
            private String code;

            @Schema(description = "업체명")
            private String name;

            @Schema(description = "유의사항")
            private String notice;
        }
    }
}
