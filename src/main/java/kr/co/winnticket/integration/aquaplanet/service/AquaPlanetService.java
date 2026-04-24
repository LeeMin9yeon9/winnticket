package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.client.AquaPlanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.*;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaPlanetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AquaPlanetService {

    private final AquaPlanetMapper mapper;
    private final AquaPlanetClient client;

    @Transactional
    public List<AquaPlanetIssueResponse> issueOrder(UUID orderId) {

        List<AquaPlanetIssueRequest> targets = mapper.selectIssueTargets(orderId);

        if (targets == null || targets.isEmpty()) {
            log.info("[AquaPlanet][ISSUE] 발행 대상 없음. orderId={}", orderId);
            return List.of();
        }

        List<AquaPlanetIssueResponse> results = new ArrayList<>();

        for (AquaPlanetIssueRequest target : targets) {
            try {
                AquaPlanetIssueResponse response = client.issue(target);
                response.setTicketId(target.getTicketId());

                // 바코드 번호(reprCponIndictNo)가 비어있으면 발행 실패로 간주하고 롤백
                // → 고객이 바코드 없는 쿠폰을 수령하는 사고 방지
                if (response.getReprCponIndictNo() == null || response.getReprCponIndictNo().isBlank()) {
                    log.error(
                            "[AquaPlanet][ISSUE] 바코드 번호 없음. orderId={}, ticketId={}, response={}",
                            orderId,
                            target.getTicketId(),
                            response
                    );
                    throw new RuntimeException("아쿠아플라넷 발행 응답에 바코드 번호가 없습니다.");
                }

                mapper.updateAquaPlanetTicket(
                        target.getTicketId(),
                        response.getReprCponIndictNo(),
                        response.getReprCponSeq()
                );

                results.add(response);

                log.info(
                        "[AquaPlanet][ISSUE] 발행 성공. orderId={}, ticketId={}, reprCponIndictNo={}, reprCponSeq={}",
                        orderId,
                        target.getTicketId(),
                        response.getReprCponIndictNo(),
                        response.getReprCponSeq()
                );

            } catch (Exception e) {
                log.error(
                        "[AquaPlanet][ISSUE] 발행 실패. orderId={}, ticketId={}, goodsNo={}",
                        orderId,
                        target.getTicketId(),
                        target.getGoodsNo(),
                        e
                );
                throw e;
            }
        }

        return results;
    }

    @Transactional
    public void cancelOrder(UUID orderId) {

        List<AquaPlanetRecallRequest> targets = mapper.selectCancelTargets(orderId);

        if (targets == null || targets.isEmpty()) {
            log.info("[AquaPlanet][CANCEL] 취소 대상 없음. orderId={}", orderId);
            return;
        }

        for (AquaPlanetRecallRequest target : targets) {
            try {
                AquaPlanetRecallResponse recallResponse = client.checkRecall(target);

                if (!recallResponse.isCancelable()) {
                    log.error(
                            "[AquaPlanet][CANCEL] 취소 불가 상태. orderId={}, ticketId={}, reprCponIndictNo={}",
                            orderId,
                            target.getTicketId(),
                            target.getReprCponIndictNo()
                    );
                    throw new RuntimeException("사용되었거나 폐기된 쿠폰은 취소할 수 없습니다.");
                }

            } catch (Exception e) {
                log.error(
                        "[AquaPlanet][CANCEL] 회수조회 실패. orderId={}, ticketId={}, reprCponIndictNo={}",
                        orderId,
                        target.getTicketId(),
                        target.getReprCponIndictNo(),
                        e
                );
                throw e;
            }
        }

        for (AquaPlanetRecallRequest target : targets) {
            try {
                AquaPlanetCancelRequest cancelRequest = new AquaPlanetCancelRequest();
                cancelRequest.setTicketId(target.getTicketId());
                cancelRequest.setCorpCd(target.getCorpCd());
                cancelRequest.setContNo(target.getContNo());
                cancelRequest.setReprCponIndictNo(target.getReprCponIndictNo());

                AquaPlanetCancelResponse cancelResponse = client.cancel(cancelRequest);

                if (!"00".equals(cancelResponse.getResultCode()) && !"0".equals(cancelResponse.getResultCode())) {
                    log.error(
                            "[AquaPlanet][CANCEL] 취소 응답 실패. orderId={}, ticketId={}, resultCode={}, resultMsg={}",
                            orderId,
                            target.getTicketId(),
                            cancelResponse.getResultCode(),
                            cancelResponse.getResultMsg()
                    );
                    throw new RuntimeException("아쿠아플라넷 취소 실패: " + cancelResponse.getResultMsg());
                }

                log.info(
                        "[AquaPlanet][CANCEL] 취소 성공. orderId={}, ticketId={}, reprCponIndictNo={}",
                        orderId,
                        target.getTicketId(),
                        target.getReprCponIndictNo()
                );

            } catch (Exception e) {
                log.error(
                        "[AquaPlanet][CANCEL] 취소 실패. orderId={}, ticketId={}, reprCponIndictNo={}",
                        orderId,
                        target.getTicketId(),
                        target.getReprCponIndictNo(),
                        e
                );
                throw e;
            }
        }
    }
}