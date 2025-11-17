package kr.co.winnticket.community.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.community.notice.dto.NoticeDetailGetResDto;
import kr.co.winnticket.community.notice.dto.NoticeListGetResDto;
import kr.co.winnticket.community.notice.dto.NoticePatchReqDto;
import kr.co.winnticket.community.notice.dto.NoticePostReqDto;
import kr.co.winnticket.community.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "공지사항", description = "공지사항 관리")
@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    // 공지사항 목록조회
    @GetMapping("api/community/notice")
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    public List<NoticeListGetResDto> getNoticeList (
        @Parameter(description = "제목") @RequestParam(value = "title", required = false, defaultValue="") String asTitle,
        @Parameter(description = "시작일자", example = "2025-11-01") @RequestParam(value = "begDate") LocalDate asBegDate,
        @Parameter(description = "종료일자", example = "2025-11-30") @RequestParam(value = "endDate") LocalDate asEndDate
    ) throws Exception {
        return service.selectNoticeList(asTitle, asBegDate, asEndDate);
    }

    // 공지사항 상세조회
    @GetMapping("api/community/notice/{id}")
    @Operation(summary = "공지사항 상세 조회", description = "전달받은 id의 공지사항을 조회합니다.")
    public NoticeDetailGetResDto getNoticeDetail (
        @Parameter(description = "게시글 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return service.selectNoticeDetail(auId);
    }

    // 공지사항 등록
    @PostMapping("api/community/notice")
    @ResponseBody
    @Operation(summary = "공지사항 등록", description = "전달받은 공지사항의 정보를 등록합니다.")
    public void postNotice (
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 정보") @RequestBody @Valid NoticePostReqDto model
    ) throws Exception {
        service.insertNotice(model);
    }

    // 공지사항 수정
    @PatchMapping("api/community/notice/{id}")
    @ResponseBody
    @Operation(summary = "공지사항 수정", description = "전달받은 공지사항의 정보를 수정합니다.")
    public void patchNotice (
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,  
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 정보") @RequestBody @Valid NoticePatchReqDto model
    ) throws Exception {
        service.updateNotice(auId, model);
    }
    
    // 공지사항 삭제
    @DeleteMapping("api/community/notice/{id}")
	@ResponseBody
	@Operation(summary = "공지사항 삭제", description = "전달받은 공지사항의 정보를 삭제합니다.")
	public void deleteNotice(
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteNotice(auId);
	}
}
