package kr.co.winnticket.order.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[주문 > 취소 요청] OrderCancelRequestReqDto")
public class OrderCancelRequestReqDto {

    @Schema(description = "환불받을 은행명 (무통장입금 결제만 필수)", example = "국민은행")
    private String bankName;

    @Schema(description = "환불받을 계좌번호 (무통장입금 결제만 필수)", example = "123456-78-901234")
    private String accountNumber;

    @Schema(description = "예금주명 (무통장입금 결제만 필수)", example = "홍길동")
    private String accountHolder;
}
