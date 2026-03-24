package kr.co.winnticket.siteinfo.terms.service;


import kr.co.winnticket.siteinfo.terms.dto.TermsReqDto;
import kr.co.winnticket.siteinfo.terms.dto.TermsResDto;
import kr.co.winnticket.siteinfo.terms.mapper.TermsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermsService {

    private final TermsMapper mapper;

    // 전체 조회 (관리자용)
    public List<TermsResDto> getAllTerms() {
        return mapper.findAll();
    }


    // 노출 약관 (공개용)
    public List<TermsResDto> getVisibleTerms() {
        return mapper.findVisible();
    }

    // 필수 약관 (회원가입용)
    public List<TermsResDto> getRequiredTerms() {
        return mapper.findRequired();
    }

    // 단일 조회
    public TermsResDto getTerms(Long id) {
        TermsResDto res = mapper.findById(id);
        if (res == null) throw new RuntimeException("약관을 찾을 수 없습니다.");
        return res;
    }


    // 등록
    @Transactional
    public TermsResDto createTerms(TermsReqDto req, String username) {

        // 해당 순서에 이미 항목이 있을 때만 밀기 (빈 자리면 그냥 삽입)
        if (req.getDisplayOrder() != null && mapper.existsByDisplayOrder(req.getDisplayOrder())) {
            mapper.increaseDisplayOrder(req.getDisplayOrder());
        }

        mapper.insert(req, username);

        return mapper.findAll().get(0);
    }

    // 수정
    @Transactional
    public TermsResDto updateTerms(Long id, TermsReqDto req, String username) {

        if (!mapper.exists(id)) {
            throw new RuntimeException("약관을 찾을 수 없습니다.");
        }

        // displayOrder 변경 시: 기존 자리 빼고 → 새 자리가 차 있으면 밀기
        if (req.getDisplayOrder() != null) {
            TermsResDto current = mapper.findById(id);
            Integer oldOrder = current.getDisplayOrder();
            Integer newOrder = req.getDisplayOrder();

            if (oldOrder != null && !oldOrder.equals(newOrder)) {
                // 1) 기존 자리 뒤쪽 당기기
                mapper.decreaseDisplayOrder(oldOrder);
                // 2) 새 자리가 차 있으면 밀기
                if (mapper.existsByDisplayOrderExcluding(newOrder, id)) {
                    mapper.increaseDisplayOrder(newOrder);
                }
            }
        }

        mapper.update(id, req, username);

        return mapper.findById(id);
    }

    // 삭제
    @Transactional
    public void deleteTerms(Long id) {
        TermsResDto terms = mapper.findById(id);
        if (terms == null) {
            throw new RuntimeException("약관을 찾을 수 없습니다.");
        }
        mapper.delete(id);

        //
        if (terms.getDisplayOrder() != null) {
            mapper.decreaseDisplayOrder(terms.getDisplayOrder());
        }
    }
}