package kr.co.winnticket.community.faq.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.community.faq.dto.FaqDetailGetResDto;
import kr.co.winnticket.community.faq.dto.FaqListGetResDto;
import kr.co.winnticket.community.faq.dto.FaqPatchReqDto;
import kr.co.winnticket.community.faq.dto.FaqPostReqDto;
import kr.co.winnticket.community.faq.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "FAQ", description = "FAQ 관리")
@RestController
@RequiredArgsConstructor
public class FaqController {
    private final FaqService service;

    // FAQ 목록조회
    @GetMapping("api/community/faq")
    @Operation(summary = "FAQ 목록 조회", description = "FAQ 목록을 조회합니다.")
    public List<FaqListGetResDto> getFaqList (
        @Parameter(description = "제목") @RequestParam(value = "title", required = false) String asTitle,
        @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) LocalDate asBegDate,
        @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) LocalDate asEndDate
    ) throws Exception {
        return service.selectFaqList(asTitle, asBegDate, asEndDate);
    }

    // FAQ 상세조회
    @GetMapping("api/community/faq/{id}")
    @Operation(summary = "FAQ 상세 조회", description = "전달받은 id의 FAQ을 조회합니다.")
    public FaqDetailGetResDto getFaqDetail (
        @Parameter(description = "게시글 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return service.selectFaqDetail(auId);
    }

    // FAQ 등록
    @PostMapping("api/community/faq")
    @ResponseBody
    @Operation(summary = "FAQ 등록", description = "전달받은 FAQ의 정보를 등록합니다.")
    public void postFaq (
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "FAQ 정보") @RequestBody @Valid FaqPostReqDto model
    ) throws Exception {
        service.insertFaq(model);
    }

    // FAQ 수정
    @PatchMapping("api/community/faq/{id}")
    @ResponseBody
    @Operation(summary = "FAQ 수정", description = "전달받은 FAQ의 정보를 수정합니다.")
    public void patchFaq (
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,  
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "FAQ 정보") @RequestBody @Valid FaqPatchReqDto model
    ) throws Exception {
        service.updateFaq(auId, model);
    }
    
    // FAQ 삭제
    @DeleteMapping("api/community/faq/{id}")
	@ResponseBody
	@Operation(summary = "FAQ 삭제", description = "전달받은 FAQ의 정보를 삭제합니다.")
	public void deleteFaq(
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteFaq(auId);
	}
}
