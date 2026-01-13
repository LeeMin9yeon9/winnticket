package kr.co.winnticket.integration.benepia.batch.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BenepiaBatchMapper {
    // 베네피아 전송 대상 상품 id 조회
    List<Map<String,Object>> selectBenepiaIds();

    // 베네피아 전송 대상 상품 정보 조회
    List<Map<String, Object>> selectBenepiaProducts();
}
