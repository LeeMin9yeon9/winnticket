package kr.co.winnticket.community.event.service;

import kr.co.winnticket.community.event.dto.EventDetailGetResDto;
import kr.co.winnticket.community.event.dto.EventListGetResDto;
import kr.co.winnticket.community.event.dto.EventPatchReqDto;
import kr.co.winnticket.community.event.dto.EventPostReqDto;
import kr.co.winnticket.community.event.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventMapper mapper;

    // 이벤트 목록조회
    public List<EventListGetResDto> selectEventList(String asTitle, LocalDate asBegDate, LocalDate asEndDate) {
        List<EventListGetResDto> lModel = mapper.selectEventList(asTitle, asBegDate, asEndDate);

        return lModel;
    }

    // 이벤트 상세조회
    public EventDetailGetResDto selectEventDetail(UUID auId) {
        EventDetailGetResDto model = mapper.selectEventDetail(auId);

        return model;
    }

    // 이벤트 등록
    public void insertEvent(EventPostReqDto model) {
        mapper.insertEvent(model);
    }

    // 이벤트 수정
    public void updateEvent(UUID auId, EventPatchReqDto model) {
        mapper.updateEvent(auId, model.getTitle(), model.getContent(), model.getEventEndDate());
    }

    // 이벤트 삭제
    public void deleteEvent(UUID auId) {
        mapper.deleteEvent(auId);
    }
}
