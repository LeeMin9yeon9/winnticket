package kr.co.winnticket.community.notice.mapper;

import kr.co.winnticket.community.notice.dto.NoticeDetailGetResDto;
import kr.co.winnticket.community.notice.dto.NoticeListGetResDto;
import kr.co.winnticket.community.notice.dto.NoticePatchReqDto;
import kr.co.winnticket.community.notice.dto.NoticePostReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface NoticeMapper {
    // 공지사항 목록 조회
    List<NoticeListGetResDto> selectNoticeList(
            @Param("title") String asTitle,
            @Param("begDate") String asBegDate,
            @Param("endDate") String asEndDate
    );

    // 공지사항 상세 조회
    List<NoticeDetailGetResDto> selectNoticeDetail(
            @Param("id") UUID auId
    );

    // 공지사항 등록
    void insertNotice(NoticePostReqDto model);

    // 공지사항 수정
    void updateNotice(NoticePatchReqDto model);
}