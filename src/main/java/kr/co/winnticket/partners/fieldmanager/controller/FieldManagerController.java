package kr.co.winnticket.partners.fieldmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "현장관리자 목록", description = "담당 현장관리자를 확인합니다.")
    public ResponseEntity<List<FieldManagerListGetResDto>> getManagerByPartner(
            @PathVariable String partnerId
    ){
        return ResponseEntity.ok(service.getListByPartner(partnerId));
    }

    @GetMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 상세조회", description = "담당 현장관리자 상세정보를 조회합니다.")
    public ResponseEntity<FieldManagerResDto> getManagerDetail(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.getDetail(id));
    }

    @PostMapping("/{partnerId}/fieldManager")
    @Operation(summary = "현장관리자 추가" , description = "담당 현장관리자를 추가합니다.")
    public ResponseEntity<FieldManagerResDto> createManager(
            @PathVariable String partnerId,
            @RequestBody FieldManagerInsertPostDto model
    ){
        model.setPartnerId(partnerId);
        return ResponseEntity.ok(service.create(model));
    }

    @PatchMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 수정" , description = "담당 현장관리자를 수정합니다.")
    public ResponseEntity<FieldManagerResDto> updateManager(
            @PathVariable UUID id,
            @RequestBody UpdateFieldManagerDto model
            ){
        return ResponseEntity.ok(service.update(id,model));
    }

    @PutMapping("/{partnerId}/fieldManager/{id}/password")
    @Operation(summary = "현장관리자 비밀번호 수정" , description = "담당 현장관리자를 비밀번호를 수정합니다.")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @RequestBody ChangePasswordDto model
    ){
        service.changePassword(id,model);
        return ResponseEntity.noContent().build();
    }



    @PutMapping("/{partnerId}/fieldManager/{id}/resetPassword")
    @Operation(summary = "관리자 비밀번호 초기화" , description = "담당 현장관리자를 비밀번호를 초기화합니다.")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID id,
            @RequestBody ResetPasswordDto model
    ){
        service.resetPassword(id,model);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{partnerId}/fieldManager/{id}")
    @Operation(summary = "현장관리자 삭제" , description = "담당 현장관리자를 삭제합니다.")
    public ResponseEntity<Void> deleteManager(
            @PathVariable UUID id
    ){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}
