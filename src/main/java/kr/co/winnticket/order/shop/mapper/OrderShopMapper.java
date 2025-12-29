package kr.co.winnticket.order.shop.mapper;

import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.UUID;

@Mapper
public interface OrderShopMapper {
    // 주문테이블 insert
    Map<String, Object> insertOrder(
            @Param("channelId") UUID channelId,
            @Param("customerName") String customerName,
            @Param("customerPhone") String customerPhone,
            @Param("customerEmail") String customerEmail,
            @Param("totalPrice") int totalPrice,
            @Param("discountPrice") int discountPrice
    );

    // 주문별 상품별 insert
    UUID insertOrderItem(
            @Param("orderId") UUID orderId,
            @Param("productId") UUID productId,
            @Param("productName") String productName,
            @Param("quantity") int quantity,
            @Param("unitPrice") int unitPrice,
            @Param("totalPrice") int totalPrice
    );

    // 주문 상품별 옵션 insert
    void insertOrderItemOption(
            @Param("orderItemId") UUID orderItemId,
            @Param("optionName") String name,
            @Param("optionValueName") String value,
            @Param("additionalPrice") int additionalPrice
    );

    // 총 가격 update
    void updateOrderPrice(
            @Param("orderId") UUID orderId,
            @Param("finalPrice") int finalPrice
    );
}
