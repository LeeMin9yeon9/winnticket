package kr.co.winnticket.partners.fieldmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.partners.fieldmanager.dto.*;
import kr.co.winnticket.partners.fieldmanager.service.FieldManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name="현장관리자", description = "파트너 관리 > 파트너 목록 / 현장관리자")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/partners")
public class FieldManagerController {

    private final FieldManagerService service;

    @GetMapping("/{partnerId}/fieldManager")
    @Operation(summary = "현장관리자 목록")
    public ResponseEntity<ApiResponse<List<FieldManagerListGetResDto>>> getManagerByPartner(
            @PathVariable String partnerId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(service.getListByPartner(partnerId))
        );
    }

    @GetMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 상세조회")
    public ResponseEntity<ApiResponse<FieldManagerResDto>> getManagerDetail(
            @PathVariable UUID partnerId,
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(
                ApiResponse.success(service.getDetail(partnerId,id))
        );
    }

    @PostMapping("/{partnerId}/fieldManager")
    @Operation(summary = "현장관리자 추가")
    public ResponseEntity<ApiResponse<FieldManagerResDto>> createManager(
            @PathVariable UUID partnerId,
            @RequestBody FieldManagerInsertPostDto dto
    ){
        return ResponseEntity.ok(ApiResponse.success("현장관리자가 생성되었습니다.", service.create(partnerId,dto))
        );
    }

    @PatchMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 수정")
    public ResponseEntity<ApiResponse<FieldManagerResDto>> updateManager(
            @PathVariable UUID partnerId,
            @PathVariable UUID id,
            @RequestBody UpdateFieldManagerDto model
    ){
        return ResponseEntity.ok(
                ApiResponse.success("현장관리자가 수정되었습니다.", service.update(partnerId,id, model))
        );
    }

    @PutMapping("/{partnerId}/fieldManager/{id}/password")
    @Operation(summary = "현장관리자 비밀번호 수정")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID partnerId,
            @PathVariable UUID id,
            @RequestBody ChangePasswordDto model
    ){
        service.changePassword(partnerId,id, model);
        return ResponseEntity.ok(
                ApiResponse.success("비밀번호가 변경되었습니다.", null)
        );
    }

    @PutMapping("/{partnerId}/fieldManager/{id}/resetPassword")
    @Operation(summary = "관리자 비밀번호 초기화")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable UUID partnerId,
            @PathVariable UUID id,
            @RequestBody ResetPasswordDto model
    ){
        service.resetPassword(partnerId,id, model);
        return ResponseEntity.ok(
                ApiResponse.success("비밀번호가 초기화되었습니다.", null)
        );
    }

    @DeleteMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteManager(
            @PathVariable UUID partnerId,
            @PathVariable UUID id
    ){
        service.delete(partnerId,id);
        return ResponseEntity.ok(
                ApiResponse.success("현장관리자가 삭제되었습니다.", null)
        );
    }
}
