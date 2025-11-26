package kr.co.winnticket.siteinfo.bankaccount.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountRequest;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResponse;
import kr.co.winnticket.siteinfo.bankaccount.service.BankAccountService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "은행정보", description = "계좌 관리")
@RestController
@RequestMapping("/api/admin/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    // 전체 조회 (관리자용)
    @GetMapping
    public ApiResponse<List<BankAccountResponse>> getAllBankAccounts() {
        List<BankAccountResponse> accounts = bankAccountService.getAllBankAccounts();
        return ApiResponse.success("계좌 목록 조회 성공", accounts);
    }

    // 노출 계좌만 조회 (공개용)
    @GetMapping("/visible")
    public ApiResponse<List<BankAccountResponse>> getVisibleBankAccounts() {
        List<BankAccountResponse> accounts = bankAccountService.getVisibleBankAccounts();
        return ApiResponse.success("노출 계좌 목록 조회 성공", accounts);
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ApiResponse<BankAccountResponse> getBankAccount(@PathVariable Long id) {
        BankAccountResponse account = bankAccountService.getBankAccount(id);
        return ApiResponse.success("계좌 정보 조회 성공", account);
    }

    // 등록
    @PostMapping
    public ApiResponse<BankAccountResponse> createBankAccount(@RequestBody BankAccountRequest request) {
        String username = "system"; // 나중에 로그인 붙이면 변경
        BankAccountResponse account = bankAccountService.createBankAccount(request, username);
        return ApiResponse.success("계좌 등록 성공", account);
    }

    // 수정
    @PutMapping("/{id}")
    public ApiResponse<BankAccountResponse> updateBankAccount(
            @PathVariable Long id,
            @RequestBody BankAccountRequest request
    ) {
        String username = "system";
        BankAccountResponse account = bankAccountService.updateBankAccount(id, request, username);
        return ApiResponse.success("계좌 수정 성공", account);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBankAccount(@PathVariable Long id) {
        bankAccountService.deleteBankAccount(id);
        return ApiResponse.success("계좌 삭제 성공", null);
    }
}