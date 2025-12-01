package kr.co.winnticket.partner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import kr.co.winnticket.partner.dto.PartnerInfoGetResDto;
import kr.co.winnticket.partner.dto.PartnerListGetResDto;
import kr.co.winnticket.partner.dto.PartnerPostReqDto;
import kr.co.winnticket.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "파트너", description = "파트너 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService service;

    @GetMapping
    @Operation(summary = "파트너 목록 조회", description = "파트너 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PartnerListGetResDto>>> getPartnerList(
            @Parameter(description = "검색어") @RequestParam(required = false) String keyword,
            @Parameter(description = "상태") @RequestParam(required = false, name = "AllStatus") PartnerStatus scStatus,
            @Parameter(description = "타입") @RequestParam(required = false, name = "AllType") PartnerType scType
    ) {
        List<PartnerListGetResDto> list = service.selectPartnerList(keyword, scStatus, scType);
        return ResponseEntity.ok(ApiResponse.success(list));
    }


    @GetMapping("/{id}")
    @Operation(summary = "파트너 기본 정보", description = "파트너의 기본 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<PartnerInfoGetResDto>> getPartnerInfo(
            @Parameter(description = "파트너 ID")
            @PathVariable UUID id
    ) {
        PartnerInfoGetResDto info = service.selectPartnerInfo(id);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @PostMapping
    @Operation(summary = "파트너 등록", description = "파트너를 등록합니다.")
    public ResponseEntity<ApiResponse<UUID>> postPartner(
            @RequestBody PartnerPostReqDto postReqDto
    ) {
        UUID createdId = service.insertPartner(postReqDto);
        return ResponseEntity.ok(ApiResponse.success("파트너가 등록되었습니다.", createdId));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "파트너 수정" , description = "파트너를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> patchPartner(
            @Parameter(description = "파트너ID")
            @PathVariable("id") UUID id,
            @RequestBody PartnerPostReqDto postReqDto
    )throws Exception{
        service.updatePartner(id, postReqDto);
        return ResponseEntity.ok(ApiResponse.success("파트너가 수정되었습니다.", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "파트너 삭제" , description = "파트너를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deletePartner(
            @Parameter(description = "파트너ID")
            @PathVariable("id") UUID id
    ) {
        service.deletePartner(id);
        return ResponseEntity.ok(ApiResponse.success("파트너가 삭제되었습니다.", null));
    }

    @PutMapping("/restore/{id}")
    @Operation(summary = "파트너 복구" , description = "파트너 및 해당 상품 복구합니다.")
    public ResponseEntity<ApiResponse<Void>> restorePartner(
            @PathVariable("id") UUID id
    ) {
        service.restorePartner(id);
        return ResponseEntity.ok(ApiResponse.success("파트너가 복구되었습니다.", null));
    }

    @PatchMapping("/status/{id}")
    @Operation(summary = "파트너 활성 / 비활성 토글" , description = "파트너를 활성/비활성화합니다.")
    public ResponseEntity<ApiResponse<Void>> updatePartnerStatus(
            @PathVariable("id") UUID id,
            @RequestBody PartnerStatus status
    ){
        service.updatePartnerStatus(id,status);
        return ResponseEntity.ok(ApiResponse.success("파트너 상태가 변경되었습니다.",null));
    }


}
