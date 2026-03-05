package kr.co.winnticket.order.shop.service;

import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.mapper.PriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceMapper priceMapper;

    /**
     * 숙박형 가격 계산
     */
    public int calculateStayUnitPrice(
            List<OrderCreateReqDto.OrderItemOptionReqDto> options,
            List<LocalDate> stayDates
    ) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("숙박형 상품은 옵션(객실타입 등)이 필요합니다.");
        }
        if (stayDates == null || stayDates.isEmpty()) {
            throw new IllegalArgumentException("숙박형 상품은 stayDates(선택 날짜 리스트)가 필요합니다.");
        }

        // 숙박은 option_value_id 하나(객실타입) 기준으로 날짜 합산
        UUID optionValueId = options.get(0).getOptionValueId();

        Integer total = priceMapper.selectStayPrice(optionValueId, stayDates);
        if (total == null || total <= 0) {
            throw new IllegalArgumentException("숙박 기간 가격이 존재하지 않습니다.");
        }
        return total;
    }

    /**
     * 일반 상품 가격 계산
     */
    public int calculateNormalPrice(
            UUID productId,
            UUID channelId,
            List<OrderCreateReqDto.OrderItemOptionReqDto> options
    ) {

        if (options != null && !options.isEmpty()) {
            List<UUID> optionValueIds = options.stream()
                    .map(OrderCreateReqDto.OrderItemOptionReqDto::getOptionValueId)
                    .collect(Collectors.toList());

            Integer optionPrice = priceMapper.selectOptionPrice(
                    productId,
                    channelId,
                    optionValueIds
            );

            if (optionPrice != null) {
                return optionPrice;
            }
        }

        Integer channelProductPrice =
                priceMapper.selectChannelProductPrice(productId, channelId);

        if (channelProductPrice != null) {
            return channelProductPrice;
        }

        return priceMapper.selectProductPrice(productId);
    }
}