package kr.co.winnticket.community.notice.service;

import kr.co.winnticket.community.notice.dto.NoticeDetailGetResDto;
import kr.co.winnticket.community.notice.dto.NoticeListGetResDto;
import kr.co.winnticket.community.notice.dto.NoticePatchReqDto;
import kr.co.winnticket.community.notice.dto.NoticePostReqDto;
import kr.co.winnticket.community.notice.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeMapper mapper;

    // 공지사항 목록조회
    public List<NoticeListGetResDto> selectNoticeList(String asTitle, String asBegDate, String asEndDate) {
        List<NoticeListGetResDto> lModel = mapper.selectNoticeList(asTitle, asBegDate, asEndDate);

        return lModel;
    }

    // 공지사항 상세조회
    public List<NoticeDetailGetResDto> selectNoticeDetail(UUID auId) {
        List<NoticeDetailGetResDto> lModel = mapper.selectNoticeDetail(auId);

        return lModel;
    }

    // 공지사항 등록
    public void insertNotice(NoticePostReqDto model) {
        mapper.insertNotice(model);
    }

    // 공지사항 수정
    public void updateNotice(NoticePatchReqDto model) {
        mapper.updateNotice(model);
    }
}
