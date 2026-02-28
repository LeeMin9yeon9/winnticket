package kr.co.winnticket.order.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartnerSplitResult {
    private boolean hasWoongin; // 웅진 상품 존재 여부
    private boolean hasPlaystory; // 플레이스토리 상품 존재 여부
    private boolean hasMair; // 엠에어 상품 존재 여부
    private boolean hasCoreworks; // 코어웍스 상품 존재 여부
    private boolean hasSmartInfini; // 스마트인피니 상품 존재 여부
    private boolean hasPlusN; // 플러스앤 상품 존재 여부
    private boolean hasAquaplanet; // 아쿠아플래닛 상품 존재 여부
    private boolean hasSpavis; // 스파비스 상품 존재 여부
    private boolean hasNormalProduct; // 일반 상품 존재 여부

}
