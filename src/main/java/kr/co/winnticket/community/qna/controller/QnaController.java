package kr.co.winnticket.community.qna.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    // QNA 상태별 카운트 조회
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
        @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) LocalDate asBegDate,
        @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) LocalDate asEndDate,
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

    // QNA 답변 등록
    @PatchMapping("api/community/qna/{id}/answer")
    @ResponseBody
    @Operation(summary = "QNA 답변등록/수정", description = "전달받은 id의 답변을 등록/수정합니다.")
    public void patchQnaAnswer (
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,  
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "답변 정보") @RequestBody @Valid QnaAnswerPatchReqDto model
    ) throws Exception {
        service.updateQnaAnswer(auId, model);
    }

    // QNA 차단
    @PatchMapping("api/community/qna/{id}/block")
    @ResponseBody
    @Operation(summary = "QNA 차단", description = "전달받은 id의 QNA를 차단합니다.")
    public void patchQnaBlock (
            @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "차단 정보") @RequestBody @Valid QnaBlockPatchReqDto model
    ) throws Exception {
        service.updateQnaBlock(auId, model);
    }

    // QNA 차단 해제
    @PatchMapping("api/community/qna/{id}/unblock")
    @ResponseBody
    @Operation(summary = "QNA 차단해제", description = "전달받은 id의 QNA를 차단해제합니다.")
    public void patchQnaUnblock (
            @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.updateQnaUnblock(auId);
    }

    // QNA 삭제
    @DeleteMapping("api/community/qna/{id}")
	@ResponseBody
	@Operation(summary = "QNA 삭제", description = "전달받은 id의 정보를 삭제합니다.")
	public void deleteQna(
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteQna(auId);
	}
}
