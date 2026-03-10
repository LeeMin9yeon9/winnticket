package kr.co.winnticket.integration.lscompany.mapper;

import kr.co.winnticket.integration.lscompany.dto.LsIssueReqDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface LsCompanyMapper {

    LsIssueReqDto selectLsIssueRequest(
            @Param("orderId") UUID orderId
    );
}
