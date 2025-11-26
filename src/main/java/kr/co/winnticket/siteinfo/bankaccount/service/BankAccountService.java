package kr.co.winnticket.siteinfo.bankaccount.service;


import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountRequest;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResponse;
import kr.co.winnticket.siteinfo.bankaccount.entity.BankAccount;
import kr.co.winnticket.siteinfo.bankaccount.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    // 전체 조회 (관리자용)
    public List<BankAccountResponse> getAllBankAccounts() {
        return bankAccountRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 노출 계좌만 조회 (공개용)
    public List<BankAccountResponse> getVisibleBankAccounts() {
        return bankAccountRepository.findByVisibleTrueOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 단일 조회
    public BankAccountResponse getBankAccount(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));
        return toResponse(bankAccount);
    }

    // 등록
    @Transactional
    public BankAccountResponse createBankAccount(BankAccountRequest request, String username) {
        BankAccount bankAccount = BankAccount.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolder(request.getAccountHolder())
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .createdBy(username)
                .updatedBy(username)
                .build();

        BankAccount saved = bankAccountRepository.save(bankAccount);
        return toResponse(saved);
    }

    // 수정
    @Transactional
    public BankAccountResponse updateBankAccount(Long id, BankAccountRequest request, String username) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("계좌 정보를 찾을 수 없습니다."));

        bankAccount.setBankName(request.getBankName());
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountHolder(request.getAccountHolder());
        bankAccount.setVisible(request.getVisible());
        bankAccount.setDisplayOrder(request.getDisplayOrder());
        bankAccount.setUpdatedBy(username);

        return toResponse(bankAccount);
    }

    // 삭제
    @Transactional
    public void deleteBankAccount(Long id) {
        if (!bankAccountRepository.existsById(id)) {
            throw new RuntimeException("계좌 정보를 찾을 수 없습니다.");
        }
        bankAccountRepository.deleteById(id);
    }

    // Entity -> DTO
    private BankAccountResponse toResponse(BankAccount entity) {
        return BankAccountResponse.builder()
                .id(entity.getId())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .accountHolder(entity.getAccountHolder())
                .visible(entity.getVisible())
                .displayOrder(entity.getDisplayOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}