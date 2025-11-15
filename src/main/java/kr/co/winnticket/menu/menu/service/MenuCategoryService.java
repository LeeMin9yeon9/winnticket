package kr.co.winnticket.menu.menu.service;

import kr.co.winnticket.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.dto.MenuSearchDto;
import kr.co.winnticket.menu.menu.dto.UpdateMenuDto;
import kr.co.winnticket.menu.menu.entity.MenuCategory;
import kr.co.winnticket.menu.menu.mapper.MenuCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryMapper menuMapper;

    // 메뉴 전체 리스트 조회
    public List<MenuListDto> MenuGetAllList() {
        List<MenuCategory> list = menuMapper.menuAllList();
        return list.stream()
                .map(m -> MenuListDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .code(m.getCode())
                        .level(m.getLevel())
                        .parentId(m.getParentId())
                        .displayOrder(m.getDisplayOrder())
                        .visible(m.getVisible())
                        .build())
                .toList();
    }

    // 메뉴 생성
    public MenuCategory MenuCreate(CreateMenuDto createMenuDto) {

        //메뉴명 필수 값 체크
        if (createMenuDto.getName() == null || createMenuDto.getName().isEmpty()) {
            throw new IllegalArgumentException("메뉴 이름은 필수 값 입니다.");
        }

        // 메뉴명 중복 체크
        if (menuMapper.countByMenuName(createMenuDto.getName()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴명입니다." + createMenuDto.getName());
        }

        // 빈 문자열이면 null 처리
        if (createMenuDto.getLevel() == 2 && createMenuDto.getParentId() == null) {
            throw new IllegalArgumentException("2레벨 parentId가 필요합니다.");
        }


        // 최상위 메뉴는 parentId = null 고정
        if (createMenuDto.getLevel() == 1) {
            createMenuDto.setParentId(null);
        }
        // 하위 메뉴인데 parentId 없음 → 에러
        if (createMenuDto.getLevel() == 2 && createMenuDto.getParentId() == null) {
            throw new IllegalArgumentException("2레벨 parentId가 필요합니다.");
        }

        // 순서 자동 설정
        Integer displayOrder = createMenuDto.getDisplayOrder();
        int nextOrder = 0;
        if(displayOrder == null){
            int maxOrder = menuMapper.menuGetMaxOrder();
            nextOrder = maxOrder + 1;
        }else{
            if(displayOrder < 1){
                throw new IllegalArgumentException("표시 순서는 항상 1 이상이여야 합니다.");
            }
        }

        UUID menuId = UUID.randomUUID();

        UUID parentUuind = createMenuDto.getParentId() == null
                ? null
                : UUID.fromString(String.valueOf(createMenuDto.getParentId()));

        MenuCategory  Menu = MenuCategory.builder()
                .id(menuId)
                .name(createMenuDto.getName())
                .code(createMenuDto.getCode())
                .level(createMenuDto.getLevel())
                .parentId(parentUuind)
                .displayOrder(displayOrder == null ? nextOrder : displayOrder)
                .visible(createMenuDto.getVisible())
                .routePath(createMenuDto.getRoutePath())
                .build();

        menuMapper.menuInsert(Menu);
        return Menu;
    }
    // 메뉴 수정
    public void updateMenu(UpdateMenuDto updateMenuDto) throws NotFoundException{
        MenuCategory menuCategory = menuMapper.menuFindById(UUID.fromString(updateMenuDto.getId().toString()));
        if(menuCategory == null){
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 변경된 항목만 저장 
        boolean changed = false;

        if (updateMenuDto.getName() != null && !updateMenuDto.getName().equals(menuCategory.getName())) {
            menuCategory.setName(updateMenuDto.getName());
            changed = true;
        }
        if (updateMenuDto.getCode() != null && !updateMenuDto.getCode().equals(menuCategory.getCode())) {
            menuCategory.setCode(updateMenuDto.getCode());
            changed = true;
        }
        if (updateMenuDto.getRoutePath() != null && !updateMenuDto.getRoutePath().equals(menuCategory.getRoutePath())) {
            menuCategory.setRoutePath(updateMenuDto.getRoutePath());
            changed = true;
        }

        if (updateMenuDto.getDisplayOrder() != null && !updateMenuDto.getDisplayOrder().equals(menuCategory.getDisplayOrder())) {
            menuCategory.setDisplayOrder(updateMenuDto.getDisplayOrder());
            changed = true;
        }

        if (updateMenuDto.getVisible() != null && !updateMenuDto.getVisible().equals(menuCategory.getVisible())) {
            menuCategory.setVisible(updateMenuDto.getVisible());
            changed = true;
        }

        if (!changed) {
            throw new RuntimeException("변경된 항목이 없습니다.");
        }
        menuMapper.menuUpdate(menuCategory);
    }

    // 메뉴삭제
    public void deleteMenu(UUID id) throws NotFoundException {
        MenuCategory menuCategory = menuMapper.menuFindById(id);
        if(menuCategory == null){
            throw new NotFoundException("삭제할 메뉴가 존재하지 않습니다.");
        }
        menuMapper.menuDelete(id);
    }

    // 메뉴조회
    public List<MenuListDto> searchMenu(MenuSearchDto searchDto){
        List<MenuCategory> menus = menuMapper.menuSearch(searchDto);
        return menus.stream()
                .map(m -> MenuListDto.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .code(m.getCode())
                        .level(m.getLevel())
                        .parentId(m.getParentId() == null ? null : m.getParentId())
                        .visible(m.getVisible())
                        .build()
                ).toList();
    }

    //노출 순서 변경
    public void changeMenu(UUID id , Integer order) throws NotFoundException{
        MenuCategory menuCategory = menuMapper.menuFindById(id);
        if(menuCategory == null){
            throw new NotFoundException("변경할 메뉴가 존재하지 않습니다.");
        }
        if(order == null || order < 1){
            throw new IllegalArgumentException("노출 순서는 1 이상의 값이어야 합니다.");
        }
        menuMapper.menuUpdateOrder(id,order);
    }

    // 메뉴 활성화 비활성화
    public void changeVisible(UUID id , Boolean visible) throws NotFoundException{
        MenuCategory menuCategory = menuMapper.menuFindById(id);
        if(menuCategory == null){
            throw new NotFoundException("변경할 메뉴가 존재하지 않습니다.");
        }
        if (visible == null){
            throw new IllegalStateException("true 또는 false 여야 합니다.");
        }
        menuMapper.menuUpdateVisible(id, visible);
    }

    //상위 메뉴로 이동
    @Transactional
    public void moveUp(UUID id) throws NotFoundException {

        MenuCategory menuCategory = menuMapper.menuFindById(id);
        if (menuCategory == null) {
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 위 메뉴 찾기
        MenuCategory upper = menuMapper.findByTreeMenu(
                menuCategory.getParentId(),
                menuCategory.getLevel(),
                menuCategory.getDisplayOrder() - 1
        );

        if (upper == null) return; // 맨 위라면 종료

        int currentOrder = menuCategory.getDisplayOrder();
        int upperOrder = upper.getDisplayOrder();

        // swap
        menuMapper.updateDisplayOrder(
                menuCategory.getId(),
                upperOrder
        );

        menuMapper.updateDisplayOrder(
                upper.getId(),
                currentOrder
        );
    }

    @Transactional
    public void moveDown(UUID id) throws NotFoundException {

        MenuCategory menuCategory = menuMapper.menuFindById(id);
        if (menuCategory == null) {
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 아래 메뉴 찾기
        MenuCategory lower = menuMapper.findByTreeMenu(
                menuCategory.getParentId(),
                menuCategory.getLevel(),
                menuCategory.getDisplayOrder() + 1
        );

        if (lower == null) return; // 맨 아래

        int currentOrder = menuCategory.getDisplayOrder();
        int lowerOrder = lower.getDisplayOrder();

        // swap
        menuMapper.updateDisplayOrder(
                menuCategory.getId(),
                lowerOrder
        );

        menuMapper.updateDisplayOrder(
                lower.getId(),
                currentOrder
        );
    }
}
