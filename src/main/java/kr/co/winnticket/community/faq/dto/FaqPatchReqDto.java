package kr.co.winnticket.community.faq.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.FaqCategory;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[FAQ > FAQ 수정] FaqPatchReqDto")
public class FaqPatchReqDto {
    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;
    
    @NotNull
    @Schema(description = "카테고리[ORDER:주문/배송관리, DELIVERY:배송, CANCEL:취소/환불, TICKET:티켓, MEMBERSHOP:회원, ETC:기타]")
    private FaqCategory category;
}
