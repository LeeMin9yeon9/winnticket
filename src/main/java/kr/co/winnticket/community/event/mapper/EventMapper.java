package kr.co.winnticket.community.event.mapper;

import kr.co.winnticket.community.event.dto.EventDetailGetResDto;
import kr.co.winnticket.community.event.dto.EventListGetResDto;
import kr.co.winnticket.community.event.dto.EventPostReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface EventMapper {
    // 이벤트 목록 조회
    List<EventListGetResDto> selectEventList(
            @Param("title") String asTitle,
            @Param("begDate") LocalDate asBegDate,
            @Param("endDate") LocalDate asEndDate
    );

    // 이벤트 상세 조회
    EventDetailGetResDto selectEventDetail(
            @Param("id") UUID auId
    );

    // 이벤트 등록
    void insertEvent(EventPostReqDto model);

    // 이벤트 수정
    void updateEvent(
        @Param("id") UUID auId, 
        @Param("title") String title,
        @Param("content") String content,
        @Param("eventEndDate") LocalDate eventEndDate
    );

    // 이벤트 삭제
    void deleteEvent(
        @Param("id") UUID auId
    );
}