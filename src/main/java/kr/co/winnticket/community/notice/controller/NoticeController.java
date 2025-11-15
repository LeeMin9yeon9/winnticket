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

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    // 공지사항 목록조회
    @GetMapping("GET/api/community/noticeList")
    @Tag(name = "0010.공지사항", description = "공지사항 관리")
    @Operation(summary = "010.공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
    public List<NoticeListGetResDto> getNoticeList (
            @Parameter(description = "제목") @RequestParam(value = "title", required = false, defaultValue="") String asTitle,
            @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) String asBegDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) String asEndDate
        ) throws Exception {

        if (asBegDate == null || asBegDate.isBlank()) {
            asBegDate = LocalDate.now().withDayOfMonth(1).toString();
        }

        if (asEndDate == null || asEndDate.isBlank()) {
            asEndDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).toString();
        }

        return service.selectNoticeList(asTitle, asBegDate, asEndDate);
    }

    // 공지사항 상세조회
    @GetMapping("GET/api/community/noticeDetail")
    @Tag(name = "0010.공지사항", description = "공지사항 관리")
    @Operation(summary = "020.공지사항 상세 조회", description = "전달받은 id의 공지사항을 조회합니다.")
    public List<NoticeDetailGetResDto> getNoticeDetail (
            @Parameter(description = "게시글_ID") @RequestParam(value = "id", required = false) UUID auId
    ) throws Exception {

        return service.selectNoticeDetail(auId);
    }

    // 공지사항 등록
    @PostMapping("POST/api/community/notice")
    @ResponseBody
    @Tag(name = "0010.공지사항", description = "공지사항 관리")
    @Operation(summary = "030.공지사항 등록", description = "전달받은 공지사항의 정보를 등록합니다.")
    public void postNotice (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 정보") @RequestBody @Valid NoticePostReqDto model
    ) throws Exception {
        service.insertNotice(model);
    }

    // 공지사항 수정
    @PatchMapping("PATCH/api/community/notice")
    @ResponseBody
    @Tag(name = "0010.공지사항", description = "공지사항 관리")
    @Operation(summary = "040.공지사항 수정", description = "전달받은 공지사항의 정보를 수정합니다.")
    public void patchNotice (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 정보") @RequestBody @Valid NoticePatchReqDto model
    ) throws Exception {
        service.updateNotice(model);
    }
}