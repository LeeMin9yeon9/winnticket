package kr.co.winnticket.partner.mapper;

import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import kr.co.winnticket.partner.dto.PartnerInfoGetResDto;
import kr.co.winnticket.partner.dto.PartnerListGetResDto;
import kr.co.winnticket.partner.dto.PartnerPostReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PartnerMapper {
    // 파트너 목록 조회
    List<PartnerListGetResDto> selectPartnerList(
            @Param("keyword") String keyword,
            @Param("AllStatus") PartnerStatus scStatus,
            @Param("AllType") PartnerType scType
    );

    // 파트너 상세조회
    PartnerInfoGetResDto selectPartnerInfo(
            @Param("id") UUID id
    );

    // 파트너 등록
    void insertPartner(PartnerPostReqDto model);

    // 파트너 수정
    void updatePartner(@Param("id") UUID id,
                       @Param("model") PartnerPostReqDto model);

    // 파트너 삭제
    void deletePartner(
            @Param("id")UUID id
            );

    void disableProductsByPartnerId(UUID partnerId);

    void restorePartner(UUID id);

    void restoreProductsByPartnerId(UUID partnerId);

    // 파트너 활성/비활성화
    void updatePartnerStatus(@Param("id") UUID id,
                             @Param("status") PartnerStatus status);


}
