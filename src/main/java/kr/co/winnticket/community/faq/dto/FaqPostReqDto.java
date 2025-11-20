package kr.co.winnticket.community.faq.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.FaqCategory;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[FAQ > FAQ 등록] FaqPostReqDto")
public class FaqPostReqDto {
    @NotEmpty
    @Schema(description = "제목")
    private String title;

    @NotEmpty
    @Schema(description = "내용")
    private String content;

    @NotEmpty
    @Schema(description = "작성자")
    private String authorName;

    @NotNull
    @Schema(description = "활성화여부")
    private boolean isActive;

    @NotNull
    @Schema(description = "카테고리 [ORDER:주문/배송관리, DELIVERY:배송, CANCEL:취소/환불, TICKET:티켓, MEMBERSHOP:회원, ETC:기타]")
    private FaqCategory category;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
