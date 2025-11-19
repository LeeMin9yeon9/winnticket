package kr.co.winnticket.menu.menu.mapper;

import kr.co.winnticket.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.menu.menu.dto.CreateSubMenuDto;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.dto.UpdateMenuDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MenuCategoryMapper {

    // 쇼핑몰 메뉴 조회(검색 조회)
    List<MenuListDto> findMenus(
            @Param("name") String name,
            @Param("code") String code
    );

    // ID로 메뉴 단건 조회
    MenuListDto menuFindById(
            @Param("id") UUID id);


    // 순서 찾기
    MenuListDto findByOrder(
            @Param("parentId") UUID parentId,
            @Param("displayOrder") Integer displayOrder
    );

    // 메뉴 추가
    void menuInsert(CreateMenuDto createMenuDto);

    // 히위메뉴추가
    void menuSubInsert(CreateSubMenuDto createSubMenuDto);


    // 메뉴 수정
    void menuUpdate(UUID id , UpdateMenuDto updateMenuDto);

    // 메뉴 삭제
    int menuDelete(@Param("id") UUID id);

    // 메뉴 활성/비활성화
    int menuUpdateVisible(
            @Param("id") UUID id,
            @Param("visible") Boolean visible
    );

    //1개 메뉴 노출 순서 변경
    void menuUpdateOrder(
            @Param("id") UUID id,
            @Param("displayOrder") Integer displayOrder
    );

    // 새 메뉴 생성 시 밀어내기
    int shiftDisplayOrder(
        @Param("parentId") UUID parentId,
        @Param("displayOrder" ) Integer displayOrder
    );

    int existsByCode(String code);

    Integer findMaxOrder(UUID parentId);

    int countChildMenus(
            @Param("id") UUID id
    );


}
