package kr.co.winnticket.integration.coreworks.service;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.coreworks.client.CoreWorksClient;
import kr.co.winnticket.integration.coreworks.dto.*;
import kr.co.winnticket.integration.coreworks.mapper.CoreWorksMapper;
import kr.co.winnticket.integration.coreworks.mapper.CoreWorksResponseMapper;
import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoreWorksService {

    private final CoreWorksClient client;
    private final CoreWorksProperties props;
    private final CoreWorksMapper mapper;
    private final CoreWorksResponseMapper responseMapper;

    // 1️⃣ 주문
    public CWOrderResponse order(UUID orderId) {

        CWOrderRequest req = mapper.selectCoreworksOrder(orderId);
        req.setChannelCd(props.getChannelCd());

        CWOrderResponse res = client.order(req);

        // HTTP 201 기준 → client에서 예외 안 났다면 성공이라고 가정
        // 만약 ResponseEntity 받는 구조라면 httpStatus 체크 필요
        validate(responseMapper.mapOrder(201), "코어웍스 주문 실패");

        return res;
    }

    // 2️⃣ 조회
    public CWSearchResponse search(UUID orderId) {

        CWSearchRequest req = mapper.selectCoreworksSearch(orderId);
        req.setChannelCd(props.getChannelCd());

        CWSearchResponse res = client.search(req);

        validate(responseMapper.mapSearch(res), "코어웍스 조회 실패");

        return res;
    }

    // 3️⃣ 취소
    public CWCancelResponse cancel(UUID orderId) {

        CWCancelRequest req = mapper.selectCoreworksCancel(orderId);
        req.setChannelCd(props.getChannelCd());

        CWCancelResponse res = client.cancel(req);

        validate(responseMapper.mapCancel(res), "코어웍스 취소 실패");

        return res;
    }

    // 4️⃣ 사용조회
    public CWUseSearchResponse useSearch(String start, String end) {

        CWUseSearchRequest req = new CWUseSearchRequest();
        req.setChannelCd(props.getChannelCd());
        req.setUseStartDate(start);
        req.setUseEndDate(end);

        CWUseSearchResponse res = client.useSearch(req);

        validate(responseMapper.mapUseSearch(res), "코어웍스 사용조회 실패");

        return res;
    }

    // 5️⃣ 티켓문자 재발송
    public CWMmsResendResponse mmsResend(UUID orderId) {

        CWMmsResendRequest req = mapper.selectCoreworksMmsResend(orderId);
        req.setChannelCd(props.getChannelCd());

        CWMmsResendResponse res = client.mmsResend(req);

        validate(responseMapper.mapMmsResend(200), "코어웍스 재발송 실패");

        return res;
    }

    // 공통 검증
    private void validate(IntegrationResult result, String defaultMessage) {

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    defaultMessage
                            + " - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }
    }
}
