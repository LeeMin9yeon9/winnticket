package kr.co.winnticket.cart.mapper;

import kr.co.winnticket.cart.dto.mapperDto.OptionValueViewDto;
import kr.co.winnticket.cart.dto.mapperDto.ProductCartViewDto;
import kr.co.winnticket.cart.dto.mapperDto.StayDatePriceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface ShopCartMapper {

        ProductCartViewDto selectProduct(UUID productId);

        List<OptionValueViewDto> selectOptionValues(
                @Param("list") List<UUID> optionValueIds
        );

        List<StayDatePriceDto> selectStayDatePrices(
                @Param("optionValueId") UUID optionValueId,
                @Param("dates") List<LocalDate> dates
        );
    }

