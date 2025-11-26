package kr.co.winnticket.siteinfo.terms.service;


import kr.co.winnticket.siteinfo.terms.dto.TermsRequest;
import kr.co.winnticket.siteinfo.terms.dto.TermsResponse;
import kr.co.winnticket.siteinfo.terms.entity.Terms;
import kr.co.winnticket.siteinfo.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsRepository termsRepository;

    // 전체 조회 (관리자용)
    public List<TermsResponse> getAllTerms() {
        return termsRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 노출 약관 (공개용)
    public List<TermsResponse> getVisibleTerms() {
        return termsRepository.findByVisibleTrueOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 필수 약관 (회원가입용)
    public List<TermsResponse> getRequiredTerms() {
        return termsRepository.findByRequiredTrueAndVisibleTrueOrderByDisplayOrderAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 단일 조회
    public TermsResponse getTerms(Long id) {
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("약관을 찾을 수 없습니다."));
        return toResponse(terms);
    }

    // 등록
    @Transactional
    public TermsResponse createTerms(TermsRequest request, String username) {
        Terms terms = Terms.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .required(request.getRequired() != null ? request.getRequired() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .visible(request.getVisible() != null ? request.getVisible() : true)
                .createdBy(username)
                .updatedBy(username)
                .build();

        Terms saved = termsRepository.save(terms);
        return toResponse(saved);
    }

    // 수정
    @Transactional
    public TermsResponse updateTerms(Long id, TermsRequest request, String username) {
        Terms terms = termsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("약관을 찾을 수 없습니다."));

        terms.setTitle(request.getTitle());
        terms.setContent(request.getContent());
        terms.setRequired(request.getRequired());
        terms.setDisplayOrder(request.getDisplayOrder());
        terms.setVisible(request.getVisible());
        terms.setUpdatedBy(username);

        return toResponse(terms);
    }

    // 삭제
    @Transactional
    public void deleteTerms(Long id) {
        if (!termsRepository.existsById(id)) {
            throw new RuntimeException("약관을 찾을 수 없습니다.");
        }
        termsRepository.deleteById(id);
    }

    // Entity -> DTO
    private TermsResponse toResponse(Terms entity) {
        return TermsResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .required(entity.getRequired())
                .displayOrder(entity.getDisplayOrder())
                .visible(entity.getVisible())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}