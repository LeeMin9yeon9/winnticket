package kr.co.winnticket.integration.aquaplanet.mapper;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetEnvelope;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetMessageHeader;
import org.springframework.stereotype.Component;

@Component
public class AquaplanetResponseMapper {

    public <T> IntegrationResult map(AquaplanetEnvelope<T> envelope) {

        if (envelope == null || envelope.getMessageHeader() == null) {
            return IntegrationResult.fail("NULL", "아쿠아플라넷 응답 없음");
        }

        AquaplanetMessageHeader header = envelope.getMessageHeader();

        String code = header.getMsgPrcsRsltCd();

        if ("0".equals(code)) {
            return IntegrationResult.success();
        }

        // 상세 메시지 추출 (있으면)
        String detailMessage = null;

        if (header.getMsgDataSub() != null && !header.getMsgDataSub().isEmpty()) {
            detailMessage = header.getMsgDataSub().get(0).getMsgCtns();
        }

        return IntegrationResult.fail(
                code,
                detailMessage != null ? detailMessage : "아쿠아플라넷 처리 실패"
        );
    }
}