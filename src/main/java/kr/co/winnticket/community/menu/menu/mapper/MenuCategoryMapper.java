package kr.co.winnticket.community.menu.menu.mapper;

import kr.co.winnticket.community.menu.menu.dto.UpdateMenuDto;
import kr.co.winnticket.community.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.community.menu.menu.dto.CreateSubMenuDto;
import kr.co.winnticket.community.menu.menu.dto.MenuListDto;
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
   // void menuUpdate(UUID id , UpdateMenuDto updateMenuDto);
    void menuUpdate(
            @Param("id") UUID id,
            @Param("updateMenuDto") UpdateMenuDto updateMenuDto
    );

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

    // 중복체크
    int existsByCode(String code);
    // parentId 내에서 최대 order 조회
    Integer findMaxOrder(UUID parentId);

    // 하위 메뉴 개수 체크
    int countChildMenus(
            @Param("id") UUID id
    );

    // 위로 이동 (newOrder ~ oldOrder-1) → +1
    int upMenu(
            @Param("parentId") UUID parentId,
            @Param("newOrder") Integer newOrder,
            @Param("oldOrder") Integer oldOrder
    );

    // 아래로 이동 (oldOrder+1 ~ newOrder) → -1
    int downMenu(
            @Param("parentId") UUID parentId,
            @Param("newOrder") Integer newOrder,
            @Param("oldOrder") Integer oldOrder
    );


}
