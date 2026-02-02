package kr.co.winnticket.partners.partnerinfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerInfoGetResDto;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerListGetResDto;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerPatchResDto;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerPostReqDto;
import kr.co.winnticket.partners.partnerinfo.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "파트너", description = "파트너 관리 > 파트너 목록 / 기본정보")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/partners")
public class PartnerController {

    private final PartnerService service;

    @GetMapping
    @Operation(summary = "파트너 목록 조회", description = "파트너 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PartnerListGetResDto>>> getPartnerList(

            @RequestParam(required = false) String keyword,

            @Parameter(description = "상태 (ACTIVE, INACTIVE, PENDING, SUSPENDED)")
            @RequestParam(required = false, name = "AllStatus") String scStatus,

            @Parameter(description = "타입 (VENUE, PROMOTER, AGENCY, ARTIST, CORPORATE)")
            @RequestParam(required = false, name = "AllType") String scType
    ) {

        PartnerStatus status = null;
        PartnerType type = null;

        if (scStatus != null && !scStatus.isBlank()) {
            status = PartnerStatus.valueOf(scStatus);
        }

        if (scType != null && !scType.isBlank()) {
            type = PartnerType.valueOf(scType);
        }

        List<PartnerListGetResDto> list =
                service.selectPartnerList(keyword, status, type);

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
    public ResponseEntity<ApiResponse<Void>> postPartner(
            @RequestBody PartnerPostReqDto postReqDto
    ) {
        service.insertPartner(postReqDto);
        return ResponseEntity.ok(ApiResponse.success("파트너가 등록되었습니다.", null));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "파트너 수정" , description = "파트너를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> patchPartner(
            @Parameter(description = "파트너ID")
            @PathVariable("id") UUID id,
            @RequestBody PartnerPatchResDto patchResDto
    )throws Exception{
        service.updatePartner(id, patchResDto);
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