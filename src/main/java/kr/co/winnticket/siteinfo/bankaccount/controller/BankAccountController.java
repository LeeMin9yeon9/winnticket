package kr.co.winnticket.siteinfo.bankaccount.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountReqDto;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResDto;
import kr.co.winnticket.siteinfo.bankaccount.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "은행정보", description = "계좌 관리")
@RestController
@RequestMapping("/api/admin/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService service;

    // 전체 조회 (관리자용)
    @GetMapping
    @Operation(summary = "계좌 전체 조회", description = "관리자용 전체 계좌 조회")
    public ApiResponse<List<BankAccountResDto>> getAllBankAccounts() {
        List<BankAccountResDto> accounts = service.getAllBankAccounts();
        return ApiResponse.success("계좌 목록 조회 성공", accounts);
    }

    // 노출 계좌만 조회 (공개용)
    @GetMapping("/visible")
    @Operation(summary = "노출 계좌 조회", description = "사용자용 노출 계좌 조회")
    public ApiResponse<List<BankAccountResDto>> getVisibleBankAccounts() {
        List<BankAccountResDto> accounts = service.getVisibleBankAccounts();
        return ApiResponse.success("노출 계좌 목록 조회 성공", accounts);
    }

    // 단일 조회
    @GetMapping("/{id}")
    @Operation(summary = "계좌 단건 조회")
    public ApiResponse<BankAccountResDto> getBankAccount(@PathVariable Long id) {
        BankAccountResDto account = service.getBankAccount(id);
        return ApiResponse.success("계좌 정보 조회 성공", account);
    }

    // 등록
    @PostMapping
    @Operation(summary = "계좌 등록")
    public ApiResponse<BankAccountResDto> createBankAccount(@RequestBody BankAccountReqDto request) {
        String username = "system"; // 나중에 로그인 붙이면 변경
        BankAccountResDto account = service.createBankAccount(request, username);
        return ApiResponse.success("계좌 등록 성공", account);
    }

    // 수정
    @PutMapping("/{id}")
    @Operation(summary = "계좌 수정")
    public ApiResponse<BankAccountResDto> updateBankAccount(
            @PathVariable Long id,
            @RequestBody BankAccountReqDto request
    ) {
        String username = "system";
        BankAccountResDto account = service.updateBankAccount(id, request, username);
        return ApiResponse.success("계좌 수정 성공", account);
    }

    // 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "계좌 삭제")
    public ApiResponse<Void> deleteBankAccount(@PathVariable Long id) {
        service.deleteBankAccount(id);
        return ApiResponse.success("계좌 삭제 성공", null);
    }
}