package kr.co.winnticket.integration.aquaplanet;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryItem;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryResponse;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaPlanetMapper;
import kr.co.winnticket.integration.aquaplanet.scheduler.AquaPlanetScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AquaPlanetSchedulerTest {

    @Mock
    private AquaPlanetClient aquaPlanetClient;

    @Mock
    private AquaPlanetMapper aquaPlanetMapper;

    @InjectMocks
    private AquaPlanetScheduler scheduler;

    @Test
    @DisplayName("사용(U) 쿠폰 → ticket_used=true 업데이트")
    void syncToday_usedCoupon_updatesTicketUsed() {
        AquaPlanetCouponHistoryItem item = new AquaPlanetCouponHistoryItem();
        item.setReprCponIndictNo("CPN-001");
        item.setCponUseStatCd("U");
        item.setClltDs("20260402143000");

        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        response.setDsResult(List.of(item));

        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenReturn(response);
        when(aquaPlanetMapper.updateAquaPlanetTicketUsed(eq("CPN-001"), eq("20260402143000")))
                .thenReturn(1);

        scheduler.syncToday();

        verify(aquaPlanetMapper, times(4)).updateAquaPlanetTicketUsed("CPN-001", "20260402143000");
        verify(aquaPlanetMapper, never()).updateAquaPlanetTicketUnused(any());
    }

    @Test
    @DisplayName("사용취소(C) 쿠폰 → ticket_used=false 업데이트")
    void syncToday_cancelledCoupon_updatesTicketUnused() {
        AquaPlanetCouponHistoryItem item = new AquaPlanetCouponHistoryItem();
        item.setReprCponIndictNo("CPN-002");
        item.setCponUseStatCd("C");
        item.setClltDs("20260402150000");

        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        response.setDsResult(List.of(item));

        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenReturn(response);
        when(aquaPlanetMapper.updateAquaPlanetTicketUnused(eq("CPN-002")))
                .thenReturn(1);

        scheduler.syncToday();

        verify(aquaPlanetMapper, times(4)).updateAquaPlanetTicketUnused("CPN-002");
        verify(aquaPlanetMapper, never()).updateAquaPlanetTicketUsed(any(), any());
    }

    @Test
    @DisplayName("사용→취소→재사용: CLLT_DS 기준 마지막 상태(U)만 적용")
    void syncToday_usedThenCancelledThenReused_appliesLastStatus() {
        AquaPlanetCouponHistoryItem used1 = new AquaPlanetCouponHistoryItem();
        used1.setReprCponIndictNo("CPN-003");
        used1.setCponUseStatCd("U");
        used1.setClltDs("20260402100000");

        AquaPlanetCouponHistoryItem cancelled = new AquaPlanetCouponHistoryItem();
        cancelled.setReprCponIndictNo("CPN-003");
        cancelled.setCponUseStatCd("C");
        cancelled.setClltDs("20260402110000");

        AquaPlanetCouponHistoryItem used2 = new AquaPlanetCouponHistoryItem();
        used2.setReprCponIndictNo("CPN-003");
        used2.setCponUseStatCd("U");
        used2.setClltDs("20260402120000");

        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        response.setDsResult(List.of(used1, cancelled, used2));

        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenReturn(response);
        when(aquaPlanetMapper.updateAquaPlanetTicketUsed(eq("CPN-003"), eq("20260402120000")))
                .thenReturn(1);

        scheduler.syncToday();

        // 마지막 상태가 U이므로 Used만 호출, Unused는 호출 안 됨
        verify(aquaPlanetMapper, times(4)).updateAquaPlanetTicketUsed("CPN-003", "20260402120000");
        verify(aquaPlanetMapper, never()).updateAquaPlanetTicketUnused(any());
    }

    @Test
    @DisplayName("빈 응답 → 업데이트 없음")
    void syncToday_emptyResponse_noUpdate() {
        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        response.setDsResult(List.of());

        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenReturn(response);

        scheduler.syncToday();

        verify(aquaPlanetMapper, never()).updateAquaPlanetTicketUsed(any(), any());
        verify(aquaPlanetMapper, never()).updateAquaPlanetTicketUnused(any());
    }

    @Test
    @DisplayName("API 호출 실패해도 다음 법인/계약 조합은 계속 처리")
    void syncToday_apiError_continuesNextCorpCont() {
        AquaPlanetCouponHistoryItem item = new AquaPlanetCouponHistoryItem();
        item.setReprCponIndictNo("CPN-004");
        item.setCponUseStatCd("U");
        item.setClltDs("20260402140000");

        AquaPlanetCouponHistoryResponse response = new AquaPlanetCouponHistoryResponse();
        response.setDsResult(List.of(item));

        // 첫 번째 호출은 실패, 나머지는 성공
        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenThrow(new RuntimeException("API 오류"))
                .thenReturn(response)
                .thenReturn(response)
                .thenReturn(response);

        scheduler.syncToday();

        // 4개 중 1개 실패, 3개 성공 → 3번 업데이트
        verify(aquaPlanetMapper, times(3)).updateAquaPlanetTicketUsed("CPN-004", "20260402140000");
    }

    @Test
    @DisplayName("4개 법인/계약 조합 모두 호출 확인")
    void syncToday_callsAllFourCorpContCombinations() {
        AquaPlanetCouponHistoryResponse empty = new AquaPlanetCouponHistoryResponse();
        empty.setDsResult(List.of());

        when(aquaPlanetClient.collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class)))
                .thenReturn(empty);

        scheduler.syncToday();

        verify(aquaPlanetClient, times(4)).collectCouponHistory(any(AquaPlanetCouponHistoryRequest.class));
    }
}
