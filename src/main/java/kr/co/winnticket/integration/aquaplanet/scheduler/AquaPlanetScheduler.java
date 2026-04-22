package kr.co.winnticket.integration.aquaplanet.scheduler;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryItem;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryRequest;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetCouponHistoryResponse;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaPlanetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AquaPlanetScheduler {

    private final AquaPlanetClient aquaPlanetClient;
    private final AquaPlanetMapper aquaPlanetMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    // corpCd, contNo 조합 (AquaPlanetSql.xml 의 pov.code CASE 와 동일)
    private static final List<String[]> CORP_CONT_LIST = List.of(
            new String[]{"4000", "11900078"},
            new String[]{"8000", "20001073"},
            new String[]{"5000", "11900033"},
            new String[]{"6000", "20000467"}
    );

    // 1시간마다 오늘 사용 이력 동기화
    @Scheduled(fixedDelay = 3600000)
    public void syncToday() {
        sync(LocalDate.now());
    }

    // 새벽 3시 어제 재조회 (사용 후 취소된 케이스 등 보정)
    @Scheduled(cron = "0 0 3 * * *")
    public void syncYesterday() {
        sync(LocalDate.now().minusDays(1));
    }

    private void sync(LocalDate date) {
        String bsnDate = date.format(DATE_FORMAT);
        log.info("[AquaPlanet] 쿠폰 사용 이력 동기화 시작 date={}", bsnDate);

        int totalUpdated = 0;

        for (String[] corpCont : CORP_CONT_LIST) {
            String corpCd = corpCont[0];
            String contNo = corpCont[1];

            try {
                AquaPlanetCouponHistoryRequest req = new AquaPlanetCouponHistoryRequest(corpCd, contNo, bsnDate);
                AquaPlanetCouponHistoryResponse res = aquaPlanetClient.collectCouponHistory(req);

                if (res == null || res.getDsResult() == null || res.getDsResult().isEmpty()) {
                    log.info("[AquaPlanet] 조회 결과 없음 corpCd={}, contNo={}, date={}", corpCd, contNo, bsnDate);
                    continue;
                }

                // 같은 쿠폰이 사용→취소→재사용될 수 있으므로 CLLT_DS 기준 오름차순 정렬 후 마지막 상태를 적용
                Map<String, AquaPlanetCouponHistoryItem> latestByIndictNo = res.getDsResult().stream()
                        .filter(item -> item.getReprCponIndictNo() != null && item.getClltDs() != null)
                        .sorted(Comparator.comparing(AquaPlanetCouponHistoryItem::getClltDs))
                        .collect(Collectors.toMap(
                                AquaPlanetCouponHistoryItem::getReprCponIndictNo,
                                item -> item,
                                (existing, replacement) -> replacement
                        ));

                for (AquaPlanetCouponHistoryItem item : latestByIndictNo.values()) {
                    try {
                        int result;
                        if ("U".equals(item.getCponUseStatCd())) {
                            String cleanClltDs = item.getClltDs().replaceAll("[^0-9]", "");

                            result = aquaPlanetMapper.updateAquaPlanetTicketUsed(
                                    item.getReprCponIndictNo(),
                                    cleanClltDs
                            );
                            if (result > 0) {
                                log.info("[AquaPlanet] 사용 처리 reprCponIndictNo={}, clltDs={}", item.getReprCponIndictNo(), item.getClltDs());
                                totalUpdated += result;
                            }
                        } else if ("C".equals(item.getCponUseStatCd())) {
                            result = aquaPlanetMapper.updateAquaPlanetTicketUnused(item.getReprCponIndictNo());
                            if (result > 0) {
                                log.info("[AquaPlanet] 사용취소 처리 reprCponIndictNo={}", item.getReprCponIndictNo());
                                totalUpdated += result;
                            }
                        }
                    } catch (Exception e) {
                        log.error("[AquaPlanet] 티켓 업데이트 오류 reprCponIndictNo={}", item.getReprCponIndictNo(), e);
                    }
                }

            } catch (Exception e) {
                log.error("[AquaPlanet] 이력 조회 오류 corpCd={}, contNo={}, date={}", corpCd, contNo, bsnDate, e);
            }
        }

        log.info("[AquaPlanet] 쿠폰 사용 이력 동기화 완료 date={}, totalUpdated={}", bsnDate, totalUpdated);
    }
}
