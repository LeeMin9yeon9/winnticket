package kr.co.winnticket.siteinfo.terms.repository;

import kr.co.winnticket.siteinfo.terms.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermsRepository extends JpaRepository<Terms, Long> {

    // 노출 약관만 (공개용)
    List<Terms> findByVisibleTrueOrderByDisplayOrderAsc();

    // 필수 + 노출 약관 (회원가입용)
    List<Terms> findByRequiredTrueAndVisibleTrueOrderByDisplayOrderAsc();

    // 전체 (관리자용)
    List<Terms> findAllByOrderByDisplayOrderAsc();
}