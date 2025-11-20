package kr.co.winnticket.community.qna.service;

import kr.co.winnticket.community.qna.dto.*;
import kr.co.winnticket.community.qna.mapper.QnaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaMapper mapper;

    // QNA 상태별 카운트 조회
    public QnaCntGetResDto selectQnaCnt() {
        QnaCntGetResDto model = mapper.selectQnaCnt();

        return model;
    }

    // QNA 목록조회
    public List<QnaListGetResDto> selectQnaList(String asTitle, LocalDate asBegDate, LocalDate asEndDate, String aqStatus) {
        List<QnaListGetResDto> lModel = mapper.selectQnaList(asTitle, asBegDate, asEndDate, aqStatus);

        return lModel;
    }

    // QNA 상세조회
    public QnaDetailGetResDto selectQnaDetail(UUID auId) {
        QnaDetailGetResDto model = mapper.selectQnaDetail(auId);

        return model;
    }

    // QNA 답변 등록
    public void updateQnaAnswer(UUID auId, QnaAnswerPatchReqDto model) {
        mapper.updateQnaAnswer(auId, model.getAnswer(), model.getAnsweredBy());
    }

    // QNA 차단
    public void updateQnaBlock(UUID auId, QnaBlockPatchReqDto model) {
        mapper.updateQnaBlock(auId, model.getBlockedReason(), model.getBlockedBy());
    }

    // QNA 차단 해제
    public void updateQnaUnblock(UUID auId) {
        mapper.updateQnaUnblock(auId);
    }

    // QNA 삭제
    public void deleteQna(UUID auId) {
        mapper.deleteQna(auId);
    }

}
