// BenepiaProductMappingService.java
package kr.co.winnticket.integration.benepia.order.service;

import kr.co.winnticket.common.enums.AgeCategory;
import kr.co.winnticket.integration.benepia.order.mapper.BenepiaOrderBatchMapper;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 베네피아 전송용 상품 라인 가공 공통 로직.
 * 실시간(BenepiaOrderService)과 일배치(BenepiaOrderBatchService)가 함께 사용한다.
 * - CASE6: prdId + optionIds(=옵션 code 조합)가 같은 라인끼리 qty/금액 병합
 * - 같은 prdId 내에서 옵션명은 같은데 optionIds가 다른 경우 "&prdOptId=" 접미로 구분
 * - optionIds에 포함된 각 옵션의 age_category(ADULT/YOUTH/CHILD)를 조회해 qty만큼 인원수 누적
 */
@Component
@RequiredArgsConstructor
public class BenepiaProductMappingService {

    private final ProductMapper productMapper;
    private final BenepiaOrderBatchMapper benepiaOrderBatchMapper;

    public static class MergedProductLine {
        public ProductDetailGetResDto detail;
        public String displayOptionName;
        public String optionIds;   // 옵션 code들을 콤마로 join한 값 (product_option_values.code 기준)
        public String prdOptNm;
        public int qty;
        public int prdPrc;
        public int adultCnt;
        public int youthCnt;
        public int childCnt;
    }

    /** 상품 상세를 매 order마다 새로 조회 (실시간용, 주문 1건 내 상품 몇 개 수준이라 캐시 불필요) */
    public List<MergedProductLine> mergeDuplicateOptions(List<OrderProductListGetResDto> items) {
        return mergeDuplicateOptions(items, new HashMap<>());
    }

    /**
     * productDetailCache를 외부에서 주입받는 버전 (배치용).
     * 배치는 하루치 전체 주문을 처리하므로, 같은 상품이 여러 주문에 반복 등장할 때
     * productMapper.selectProductDetail() 중복 호출(N+1)을 줄이기 위해 캐시를 재사용한다.
     */
    public List<MergedProductLine> mergeDuplicateOptions(
            List<OrderProductListGetResDto> items,
            Map<String, ProductDetailGetResDto> productDetailCache) {

        Map<String, MergedProductLine> merged = new LinkedHashMap<>();
        Map<String, Map<String, Set<String>>> nameCollisionCheck = new HashMap<>();

        // 1) 전체 optionIds(code 조합)를 모아서 한 번에 age_category 조회 (N+1 방지)
        Set<String> allOptionCodes = new HashSet<>();
        for (OrderProductListGetResDto p : items) {
            String ids = nvl(p.getOptionIds());
            if (!ids.isBlank()) {
                for (String code : ids.split(",")) {
                    if (!code.isBlank()) allOptionCodes.add(code.trim());
                }
            }
        }

        Map<String, String> ageCategoryByOptionCode = new HashMap<>();
        if (!allOptionCodes.isEmpty()) {
            List<Map<String, Object>> rows =
                    benepiaOrderBatchMapper.selectAgeCategoriesByOptionCodes(new ArrayList<>(allOptionCodes));
            for (Map<String, Object> row : rows) {
                ageCategoryByOptionCode.put(
                        String.valueOf(row.get("code")),
                        String.valueOf(row.get("age_category"))
                );
            }
        }

        for (OrderProductListGetResDto p : items) {
            ProductDetailGetResDto detail = productDetailCache.computeIfAbsent(
                    String.valueOf(p.getProductId()),
                    k -> productMapper.selectProductDetail(p.getProductId())
            );

            String prdId = nvl(detail.getCode());
            String optionName = nvl(p.getOptionName());
            String optionIds = nvl(p.getOptionIds());
            int qty = nvl(p.getQuantity());

            String mergeKey = prdId + "&prdOptId=" + optionIds;

            MergedProductLine line = merged.get(mergeKey);
            if (line == null) {
                line = new MergedProductLine();
                line.detail = detail;
                line.displayOptionName = optionName;
                line.optionIds = optionIds;
                merged.put(mergeKey, line);
            }

            line.qty += qty;
            line.prdPrc += nvl(p.getTotalPrice());

            // optionIds에 포함된 각 옵션 code의 age_category에 따라 qty만큼 카운트 누적
            if (!optionIds.isBlank()) {
                for (String code : optionIds.split(",")) {
                    String rawCategory = ageCategoryByOptionCode.get(code.trim());
                    AgeCategory category = parseAgeCategory(rawCategory);
                    if (category == null) continue;

                    switch (category) {
                        case ADULT -> line.adultCnt += qty;
                        case YOUTH -> line.youthCnt += qty;
                        case CHILD -> line.childCnt += qty;
                    }
                }
            }

            nameCollisionCheck
                    .computeIfAbsent(prdId, k -> new HashMap<>())
                    .computeIfAbsent(optionName, k -> new HashSet<>())
                    .add(optionIds);
        }

        for (MergedProductLine line : merged.values()) {
            String prdId = nvl(line.detail.getCode());
            Set<String> idsForThisName = nameCollisionCheck.get(prdId).get(line.displayOptionName);

            if (idsForThisName.size() > 1 && !line.optionIds.isBlank()) {
                line.prdOptNm = line.displayOptionName + "&prdOptId=" + line.optionIds;
            } else {
                line.prdOptNm = line.displayOptionName;
            }
        }

        return new ArrayList<>(merged.values());
    }

    private AgeCategory parseAgeCategory(String raw) {
        if (raw == null || raw.isBlank() || "null".equals(raw)) return null;
        try {
            return AgeCategory.valueOf(raw.trim());
        } catch (IllegalArgumentException e) {
            return null; // 알 수 없는 값은 무시
        }
    }

    private String nvl(String val) {
        return val == null ? "" : val;
    }

    private Integer nvl(Integer val) {
        return val == null ? 0 : val;
    }
}