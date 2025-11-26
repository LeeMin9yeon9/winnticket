package kr.co.winnticket.siteinfo.bankaccount.repository;

import kr.co.winnticket.siteinfo.bankaccount.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    // 노출 계좌만 표시순서 순으로
    List<BankAccount> findByVisibleTrueOrderByDisplayOrderAsc();

    // 전체 계좌 표시순서 순으로
    List<BankAccount> findAllByOrderByDisplayOrderAsc();
}