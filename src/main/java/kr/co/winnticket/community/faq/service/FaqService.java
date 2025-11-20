package kr.co.winnticket.community.faq.service;

import kr.co.winnticket.community.faq.dto.*;
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

    // 카테고리 목록조회
    public List<FaqCategoryListGetResDto> selectFaqCategoryList() {
        return mapper.selectFaqCategoryList();
    }

    // 카테고리 등록
    public void insertFaqCategory(FaqCategoryPostReqDto model) {
        mapper.insertFaqCategory(model);
    }

    // 카테고리 수정
    public void updateFaqCategory(String asId, FaqCategoryPatchReqDto model) {
        mapper.updateFaqCategory(asId, model.getName());
    }

    // 카테고리 삭제
    public void deleteFaqCategory(String asId) {
        mapper.deleteFaqCategory(asId);
    }
}
