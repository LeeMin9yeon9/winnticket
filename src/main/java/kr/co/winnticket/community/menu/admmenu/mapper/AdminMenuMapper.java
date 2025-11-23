package kr.co.winnticket.community.menu.admmenu.mapper;

import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface AdminMenuMapper {


        // 관리자 메뉴 조회 (검색 조건 포함)
        List<AdminMenuListDto> findAdminMenus(
                @Param("title") String title,
                @Param("titleEn") String titleEn,
                @Param("page") String page
        );

        // ID로 메뉴 단건 조회
        AdminMenuListDto admMenuFindById(@Param("id") UUID id);

        // 관리자 메뉴 추가
        void admMenuInsert(AdminMenuCreateDto adminMenuCreateDto);

        // 관리자 메뉴 수정
        void admMenuUpdate(
                @Param("id") UUID id,
                @Param("adminMenuUpdate") AdminMenuUpdateDto adminMenuUpdateDto);

        // 관리자 메뉴 삭제
        void admMenuDelete(@Param("id") UUID id);

        // 관리자 메뉴 노출 순서 변경
        void admMenuUpdateOrder(@Param("id") UUID id, @Param("order") Integer order);

        // 관리자 메뉴 활성/비활성화
        void admMenuUpdateVisible(@Param("id") UUID id, @Param("visible") Boolean visible);


        // 신규 메뉴 생성 시 순번 자동 계산용
        Integer admMenuGetMaxOrder();

        // 페이지 중복 여부
        int admMenuCountByPage(@Param("page") String page);

        // 메뉴명 중복 여부
        int admMenuCountByTitle(@Param("title") String title);

        // 메뉴 순서
        int shiftUp(@Param("newOrder") Integer newOrder,
                    @Param("oldOrder") Integer oldOrder);

        int shiftDown(@Param("oldOrder") Integer oldOrder,
                      @Param("newOrder") Integer newOrder);
}
