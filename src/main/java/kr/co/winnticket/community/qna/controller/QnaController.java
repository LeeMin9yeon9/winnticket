package kr.co.winnticket.community.qna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.enums.QnaStatus;
import kr.co.winnticket.community.qna.dto.*;
import kr.co.winnticket.community.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "QNA", description = "QNA 관리")
@RestController
@RequiredArgsConstructor
public class QnaController {
    private final QnaService service;

    // QNA 목록조회
    @GetMapping("api/community/qna/count")
    @Operation(summary = "QNA 상태별 카운트 조회", description = "QNA 상태별 카운트를 조회합니다.")
    public QnaCntGetResDto getQnaList (
    ) throws Exception {
        return service.selectQnaCnt();
    }

    // QNA 목록조회
    @GetMapping("api/community/qna")
    @Operation(summary = "QNA 목록 조회", description = "QNA 목록을 조회합니다.")
    public List<QnaListGetResDto> getQnaList (
        @Parameter(description = "제목") @RequestParam(value = "title", required = false, defaultValue="") String asTitle,
        @Parameter(description = "시작일자", example = "2025-11-01") @RequestParam(value = "begDate") LocalDate asBegDate,
        @Parameter(description = "종료일자", example = "2025-11-30") @RequestParam(value = "endDate") LocalDate asEndDate,
        @Parameter(description = "QNA상태 [ALL:전체, PENDING:답변대기, ANSWERED:답변완료, BLOCKED:차단]") @RequestParam(value = "status") String aqStatus
    ) throws Exception {
        return service.selectQnaList(asTitle, asBegDate, asEndDate, aqStatus);
    }

    // QNA 상세조회
    @GetMapping("api/community/qna/{id}")
    @Operation(summary = "QNA 상세 조회", description = "전달받은 id의 QNA을 조회합니다.")
    public QnaDetailGetResDto getQnaDetail (
        @Parameter(description = "게시글 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return service.selectQnaDetail(auId);
    }

    // QNA 등록
    @PostMapping("api/community/qna")
    @ResponseBody
    @Operation(summary = "QNA 등록", description = "전달받은 QNA의 정보를 등록합니다.")
    public void postQna (
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "QNA 정보") @RequestBody @Valid QnaPostReqDto model
    ) throws Exception {
        service.insertQna(model);
    }

    // QNA 수정
    @PatchMapping("api/community/qna/{id}")
    @ResponseBody
    @Operation(summary = "QNA 수정", description = "전달받은 QNA의 정보를 수정합니다.")
    public void patchQna (
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,  
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "QNA 정보") @RequestBody @Valid QnaPatchReqDto model
    ) throws Exception {
        service.updateQna(auId, model);
    }
    
    // QNA 삭제
    @DeleteMapping("api/community/qna/{id}")
	@ResponseBody
	@Operation(summary = "QNA 삭제", description = "전달받은 QNA의 정보를 삭제합니다.")
	public void deleteQna(
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteQna(auId);
	}
}
