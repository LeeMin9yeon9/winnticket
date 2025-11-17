package kr.co.winnticket.community.qna.service;

import kr.co.winnticket.community.qna.dto.QnaDetailGetResDto;
import kr.co.winnticket.community.qna.dto.QnaListGetResDto;
import kr.co.winnticket.community.qna.dto.QnaPatchReqDto;
import kr.co.winnticket.community.qna.dto.QnaPostReqDto;
import kr.co.winnticket.community.qna.mapper.QnaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaMapper mapper;

    // QNA 목록조회
    public List<QnaListGetResDto> selectQnaList(String asTitle, String asBegDate, String asEndDate) {
        List<QnaListGetResDto> lModel = mapper.selectQnaList(asTitle, asBegDate, asEndDate);

        return lModel;
    }

    // QNA 상세조회
    public QnaDetailGetResDto selectQnaDetail(UUID auId) {
        QnaDetailGetResDto model = mapper.selectQnaDetail(auId);

        return model;
    }

    // QNA 등록
    public void insertQna(QnaPostReqDto model) {
        mapper.insertQna(model);
    }

    // QNA 수정
    public void updateQna(UUID auId, QnaPatchReqDto model) {
        mapper.updateQna(auId, model.getTitle(), model.getContent());
    }

    // QNA 삭제
    public void deleteQna(UUID auId) {
        mapper.deleteQna(auId);
    }
}
