package kr.co.winnticket.community.faq.service;

import kr.co.winnticket.community.faq.dto.FaqDetailGetResDto;
import kr.co.winnticket.community.faq.dto.FaqListGetResDto;
import kr.co.winnticket.community.faq.dto.FaqPatchReqDto;
import kr.co.winnticket.community.faq.dto.FaqPostReqDto;
import kr.co.winnticket.community.faq.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqMapper mapper;

    // FAQ 목록조회
    public List<FaqListGetResDto> selectFaqList(String asTitle, LocalDate asBegDate, LocalDate asEndDate) {
        List<FaqListGetResDto> lModel = mapper.selectFaqList(asTitle, asBegDate, asEndDate);

        return lModel;
    }

    // FAQ 상세조회
    public FaqDetailGetResDto selectFaqDetail(UUID auId) {
        FaqDetailGetResDto model = mapper.selectFaqDetail(auId);

        return model;
    }

    // FAQ 등록
    public void insertFaq(FaqPostReqDto model) {
        mapper.insertFaq(model);
    }

    // FAQ 수정
    public void updateFaq(UUID auId, FaqPatchReqDto model) {
        mapper.updateFaq(auId, model.getTitle(), model.getContent(), model.getCategory());
    }

    // FAQ 삭제
    public void deleteFaq(UUID auId) {
        mapper.deleteFaq(auId);
    }
}
