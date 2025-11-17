package kr.co.winnticket.community.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.community.common.service.CommunityCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "공통", description = "공통 관리")
@RestController
@RequiredArgsConstructor
public class CommunityCommonController {
    private final CommunityCommonService service;

    // 이벤트 조회수 카운트
    @PatchMapping("api/community/common/viewCount/{id}")
    @ResponseBody
    @Operation(summary = "게시글 조회수 수정", description = "전달받은 id의 조회수를 수정합니다.")
    public void patchViewCount (
            @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.increaseViewCount(auId);
    }

    // 이벤트 활성화여부 수정
    @PatchMapping("api/community/common/isActive/{id}")
    @ResponseBody
    @Operation(summary = "게시글 활성화여부 수정", description = "전달받은 id의 활성화여부를 수정합니다.")
    public void patchIsActive (
            @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,
            @Parameter(description = "활성화여부") @RequestParam(value = "isActive") boolean abIsActive
    ) throws Exception {
        service.updateIsActive(auId, abIsActive);
    }
}
