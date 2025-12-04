package kr.co.winnticket.partners.partnerinfo.service;

import kr.co.winnticket.common.enums.PartnerStatus;
import kr.co.winnticket.common.enums.PartnerType;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerInfoGetResDto;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerListGetResDto;
import kr.co.winnticket.partners.partnerinfo.dto.PartnerPostReqDto;
import kr.co.winnticket.partners.partnerinfo.mapper.PartnerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerMapper mapper;

    // 파트너 목록 조회
    public List<PartnerListGetResDto> selectPartnerList(String keyword, PartnerStatus scStatus , PartnerType scType){
        List<PartnerListGetResDto> list = mapper.selectPartnerList(keyword,scStatus,scType);

        return list;
    }

    // 파트너 상세조회
    public PartnerInfoGetResDto selectPartnerInfo(UUID id){
        PartnerInfoGetResDto model = mapper.selectPartnerInfo(id);

        return model;
    }

    // 파트너 등록
    public UUID insertPartner(PartnerPostReqDto model){
        UUID id = UUID.randomUUID();
        model.setId(id);
        mapper.insertPartner(model);
        return id;
    }

    // 파트너 수정
    public void updatePartner(UUID id , PartnerPostReqDto model){
        mapper.updatePartner(id,model);
    }

    // 파트너 삭제
    public void deletePartner(UUID id){
        mapper.deletePartner(id);
    }

    @Transactional
    public void restorePartner(UUID id) {

      // 파트너 복구
        mapper.restorePartner(id);

        // 연관 상품 복구
        mapper.restoreProductsByPartnerId(id);
    }

    // 파트너 활성 / 비활성
    public void updatePartnerStatus(UUID id , PartnerStatus status){
        mapper.updatePartnerStatus(id,status);
    }

}
