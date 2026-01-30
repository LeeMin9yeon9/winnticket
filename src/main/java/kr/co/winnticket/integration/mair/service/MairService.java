package kr.co.winnticket.integration.mair.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.mair.client.MairCouponClient;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.props.MairProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MairService {

    private final MairOrderMapper mairOrderMapper;
    private final MairCouponClient  mairCouponClient;
    private final MairProperties mairProperties;

    // 결제 완료 티켓 발송
    @Transactional
    public void issueTickets(String orderNumber){
        System.out.println("[MAIR] issueTicketsWhenPid start orderNumber=" + orderNumber);

        MairOrderInfoDto dto = mairOrderMapper.selectOrderInfo(orderNumber);
        if(dto == null){
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        // PAID 발송
        if(!"PAID".equals(dto.getPaymentStatus())){
            System.out.println("[MAIR] skip issue. status=" + dto.getPaymentStatus());
            return;
        }
        System.out.println("[MAIR] order.status=" + dto.getPaymentStatus());

        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(dto.getOrderId());

        for(MairOrderItemInfoDto item : items) {

            String itcd = item.getProductCode();
            if (itcd == null || itcd.isBlank()) continue;

            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            if (qty <= 0) continue;

            System.out.println("[MAIR] items.size=" + items.size());


            // 중복 발송 방지
            int alreadyIssued = mairOrderMapper.countOrderTickets(item.getOrderItemId());
            if (alreadyIssued >= qty) {
                continue;
            }

            // 부족한 수량만큼 발송
            int need = qty - alreadyIssued;

            for (int i = 0; i < need; i++) {


                MairCouponResDto res = mairCouponClient.issue(
                        itcd,
                        dto.getOrderNumber(),   // TRNO
                        dto.getCustomerName(),  // ODNM
                        dto.getCustomerPhone()  // ODHP
                );

                if (res == null || !res.isOk()) {
                    throw new IllegalStateException("[MAIR] 발송 실패 orderNumber=" + orderNumber
                            + ", itcd=" + itcd
                            + ", result=" + (res == null ? "null" : res.getResult()));
                }
                // 나중에 주석제거
//                if (res.getTno() == null || res.getTno().isBlank()) {
//                    throw new IllegalStateException("[MAIR] 발송 성공했는데 쿠폰번호 없음 orderNumber=" + orderNumber + ", itcd=" + itcd);
//                }

                // 테스트용 쿠폰
                String ticketNo = res.getTno();
                if (ticketNo == null || ticketNo.isBlank()) {

                    // dev 모드면 임시 발급 처리
                    if ("dev".equalsIgnoreCase(mairProperties.getMode())) {
                        ticketNo = generateTempTicketNo(dto.getOrderNumber(), item.getOrderItemId());
                    } else {
                        // prod인데 TNO가 없으면 장애로 보는게 맞음
                        throw new IllegalStateException("엠에어 발송 성공했는데 TNO 없음 (prod) orderNumber="
                                + dto.getOrderNumber() + ", itcd=" + itcd);
                    }
                }

                // 발송된 쿠폰번호(TNO)를 티켓 테이블에 저장
                mairOrderMapper.insertOrderTicket(String.valueOf(item.getOrderItemId()),ticketNo);// 나중에추가 res.getTno()

                System.out.println("[MAIR] issued ticket orderNumber=" + orderNumber
                        + ", orderItemId=" + item.getOrderItemId()
                        + ", itcd=" + itcd
                        + ", ticketNumber=" + res.getTno());
            }
        }
    }


    // 쿠폰 취소
    @Transactional
    public void cancelByOrder(String orderNumber , UUID orderItemId){

        int cancelableCount = mairOrderMapper.countCancelableTickets(orderItemId);
        if (cancelableCount <= 0) {
            throw new IllegalStateException("취소 가능한 티켓이 없습니다. (이미 사용/취소됨) orderItemId=" + orderItemId);
        }
        // 주문 조화
        MairOrderInfoDto dto = mairOrderMapper.selectOrderInfo(orderNumber);
        if(dto == null){
            throw new IllegalArgumentException("주문이 없습니다.");
        }

        // 2) ITCD 찾기
        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(dto.getOrderId());
        MairOrderItemInfoDto target = items.stream()
                .filter(i -> orderItemId.equals(i.getOrderItemId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[MAIR]주문아이템 없음 orderItemId=" + orderItemId));

        String itcd = target.getProductCode();
        if (itcd == null || itcd.isBlank()) {
            throw new IllegalStateException("[MAIR]ITCD 없음(orderItemId=" + orderItemId + ")");
        }

        // 3) 엠에어 취소 호출
        MairCouponResDto res = mairCouponClient.cancel(itcd, orderNumber);

        if (res == null || !"OK".equalsIgnoreCase(res.getResult())) {
            throw new IllegalStateException("[MAIR] 취소 실패 orderNumber=" + orderNumber
                    + ", itcd=" + itcd
                    + ", result=" + (res == null ? "null" : res.getResult()));
        }

        // 4)미사용+미취소만 취소처리
        int updated = mairOrderMapper.updateTicketsCanceled(orderItemId);

        if (updated <= 0) {
            throw new IllegalStateException("[MAIR]취소 처리할 티켓이 없습니다. (이미 사용/취소됨) orderItemId=" + orderItemId);
        }
    }

    // 사용 여부 확인
    public void useCheckByOrderNumber(String orderNumber) {

        MairOrderInfoDto order = mairOrderMapper.selectOrderInfo(orderNumber);
        if (order == null) {
            throw new IllegalArgumentException("[MAIR]주문이 없습니다. orderNumber=" + orderNumber);
        }

        List<MairOrderItemInfoDto> items = mairOrderMapper.selectOrderItemInfos(order.getOrderId());

        for (MairOrderItemInfoDto item : items) {
            String itcd = item.getProductCode();
            if (itcd == null || itcd.isBlank()) continue;

            MairCouponResDto res = mairCouponClient.useCheck(itcd, orderNumber);

            if (res == null) {
                throw new IllegalStateException("[MAIR] 사용여부 조회 응답 null orderNumber=" + orderNumber + ", itcd=" + itcd);
            }

            System.out.println("[MAIR] useCheck orderNumber=" + orderNumber
                    + ", itcd=" + itcd
                    + ", result=" + res.getResult()
                    + ", tno=" + res.getTno());
        }
    }

    private String normalizeHp(String hp) {
        if (hp == null) return null;
        return hp.replaceAll("[^0-9]", "");
    }

    // 쿠폰 테스트용
    private String generateTempTicketNo(String orderNumber, UUID orderItemId) {
        return "DEV-MAIR-"
                + orderNumber
                + "-"
                + orderItemId.toString().substring(0, 8)
                + "-"
                + System.currentTimeMillis();
    }
}
