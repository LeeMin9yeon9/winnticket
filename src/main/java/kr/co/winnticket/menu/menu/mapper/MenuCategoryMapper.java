package kr.co.winnticket.menu.menu.mapper;

import kr.co.winnticket.menu.menu.dto.MenuSearchDto;
import kr.co.winnticket.menu.menu.entity.MenuCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MenuCategoryMapper {
    List<MenuCategory> menuAllList();

    MenuCategory menuFindById(
            @Param("id") UUID id);


    MenuCategory findByTreeMenu( // 형제메뉴중에서 순서찾는 함수
            @Param("parentId") UUID parentId,
            @Param("level") Integer level,
            @Param("displayOrder") Integer displayOrder
    );

    // 메뉴 추가
    void menuInsert(MenuCategory menuCategory);

    // 메뉴 수정
    int menuUpdate(MenuCategory menuCategory);

    // 메뉴 삭제
    MenuCategory menuDelete(
            @Param("id") UUID id);

    // 메뉴 활성/비활성화
    int menuUpdateVisible(
            @Param("id") UUID id,
            @Param("visible") Boolean visible
    );

    //메뉴 노출 순서 변경
    void menuUpdateOrder(
            @Param("id") UUID id,
            @Param("order") Integer order
    );

    // swap
    void updateDisplayOrder(
            @Param("id") UUID id,
            @Param("displayOrder") Integer displayOrder
    );

    // 신규 메뉴 생성 시 자동 순서 부여
    Integer menuGetMaxOrder();
    int countByMenuName(String name);

    // 메뉴 검색
    List<MenuCategory> menuSearch(MenuSearchDto menuSearchDto);

}
