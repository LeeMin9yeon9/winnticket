package kr.co.winnticket.siteinfo.bankaccount.service;


import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountReqDto;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResDto;
import kr.co.winnticket.siteinfo.bankaccount.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountMapper mapper;

    // 전체 조회 (관리자용)
    @Transactional
    public List<BankAccountResDto> getAllBankAccounts() {
        return mapper.findAll();
    }

    // 노출 계좌만 조회 (공개용)
    @Transactional
    public List<BankAccountResDto> getVisibleBankAccounts() {
        return mapper.findVisible();
    }


    // 단일 조회
    @Transactional
    public BankAccountResDto getBankAccount(Long id) {
        BankAccountResDto res = mapper.findById(id);
        if (res == null) {
            throw new RuntimeException("계좌 정보를 찾을 수 없습니다.");
        }
        return res;
    }

    // 등록
    @Transactional
    public BankAccountResDto createBankAccount(BankAccountReqDto req, String username) {
        // displayOrder가 지정된 경우 해당 위치 이후를 밀어낸다
        if (req.getDisplayOrder() != null) {
            mapper.shiftOrder(req.getDisplayOrder());
        }
        mapper.insert(req, username);
        return null;
    }

    // 수정
    @Transactional
    public BankAccountResDto updateBankAccount(Long id, BankAccountReqDto req, String username) {
        if (!mapper.exists(id)) {
            throw new RuntimeException("계좌 정보를 찾을 수 없습니다.");
        }
        // 순서 변경 요청 있을 때만
        if (req.getDisplayOrder() != null) {

            // 기존 데이터 조회
            BankAccountResDto current = mapper.findById(id);

            Integer oldOrder = current.getDisplayOrder();
            Integer newOrder = req.getDisplayOrder();

            // 같은 위치면 넘기고
            if (!oldOrder.equals(newOrder)) {

                // 현재 자리 뒤로 당기기
                mapper.decreaseOrderAfter(oldOrder);

                // 새 자리로 밀기
                mapper.shiftOrder(newOrder);
            }
        }

        mapper.update(id, req, username);

        return mapper.findById(id);
    }

    // 삭제
    @Transactional
    public void deleteBankAccount(Long id) {
        if (!mapper.exists(id)) {
            throw new RuntimeException("계좌 정보를 찾을 수 없습니다.");
        }
        mapper.delete(id);
        // 삭제 후 재정렬
        mapper.reorderAfterDelete();
    }
}