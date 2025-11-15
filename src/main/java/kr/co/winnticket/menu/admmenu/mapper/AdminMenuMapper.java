package kr.co.winnticket.menu.admmenu.mapper;

import kr.co.winnticket.menu.admmenu.dto.AdminMenuSearchDto;
import kr.co.winnticket.menu.admmenu.entity.AdminMenus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface AdminMenuMapper {

    // 관리자 메뉴 전체 조회
    List<AdminMenus> admMenuAllList();

    // ID로 메뉴 단건 조회
    AdminMenus admMenuFindById(
            @Param("id") UUID id);

    // 관리자 메뉴 추가
    void admMenuInsert(AdminMenus admMenu
    );
    // 관리자 메뉴 업데이트
    void admMenuUpdate(AdminMenus admMenu
    );

    // 관리자 메뉴 삭제
    void admMenuDelete(
            @Param("id") UUID id
    );
    // 관리자 메뉴 노출 순서 변경
    void admMenuUpdateOrder(
            @Param("id") UUID id,
            @Param("order") Integer order
    );

    // 관리자 메뉴 활성/비활성화
    void admMenuUpdateVisible(
            @Param("id") UUID id,
            @Param("visible") Boolean visible
    );

    // 신규 메뉴 생성 시 자동 순서 부여
    Integer admMenuGetMaxOrder();

    int admMenuCountByPage(String page); // 페이지 중복 여부
    int admMenuCountByTitle(String title); // 메뉴명 중복 여부

    // 관리자 메뉴 검색
    List<AdminMenus> admMenuSearch(AdminMenuSearchDto searchDto);


}
