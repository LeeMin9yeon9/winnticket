package kr.co.winnticket.community.event.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.community.event.dto.EventDetailGetResDto;
import kr.co.winnticket.community.event.dto.EventListGetResDto;
import kr.co.winnticket.community.event.dto.EventPatchReqDto;
import kr.co.winnticket.community.event.dto.EventPostReqDto;
import kr.co.winnticket.community.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "이벤트", description = "이벤트 관리")
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService service;

    // 이벤트 목록조회
    @GetMapping("api/community/event")
    @Operation(summary = "이벤트 목록 조회", description = "이벤트 목록을 조회합니다.")
    public List<EventListGetResDto> getEventList (
        @Parameter(description = "제목") @RequestParam(value = "title", required = false, defaultValue="") String asTitle,
        @Parameter(description = "시작일자", example = "2025-11-01") @RequestParam(value = "begDate") LocalDate asBegDate,
        @Parameter(description = "종료일자", example = "2025-11-30") @RequestParam(value = "endDate") LocalDate asEndDate
    ) throws Exception {
        return service.selectEventList(asTitle, asBegDate, asEndDate);
    }

    // 이벤트 상세조회
    @GetMapping("api/community/event/{id}")
    @Operation(summary = "이벤트 상세 조회", description = "전달받은 id의 이벤트을 조회합니다.")
    public EventDetailGetResDto getEventDetail (
        @Parameter(description = "게시글 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return service.selectEventDetail(auId);
    }

    // 이벤트 등록
    @PostMapping("api/community/event")
    @ResponseBody
    @Operation(summary = "이벤트 등록", description = "전달받은 이벤트의 정보를 등록합니다.")
    public void postEvent (
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "이벤트 정보") @RequestBody @Valid EventPostReqDto model
    ) throws Exception {
        service.insertEvent(model);
    }

    // 이벤트 수정
    @PatchMapping("api/community/event/{id}")
    @ResponseBody
    @Operation(summary = "이벤트 수정", description = "전달받은 이벤트의 정보를 수정합니다.")
    public void patchEvent (
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId,  
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "이벤트 정보") @RequestBody @Valid EventPatchReqDto model
    ) throws Exception {
        service.updateEvent(auId, model);
    }
    
    // 이벤트 삭제
    @DeleteMapping("api/community/event/{id}")
	@ResponseBody
	@Operation(summary = "이벤트 삭제", description = "전달받은 이벤트의 정보를 삭제합니다.")
	public void deleteEvent(
        @Parameter(description = "게시글_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteEvent(auId);
	}
}
