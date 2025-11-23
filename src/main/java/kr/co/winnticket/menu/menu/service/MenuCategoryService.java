package kr.co.winnticket.menu.menu.service;

import kr.co.winnticket.menu.common.MenuValidator;
import kr.co.winnticket.menu.menu.dto.*;
import kr.co.winnticket.menu.menu.mapper.MenuCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuCategoryService {

    private final MenuCategoryMapper menuMapper;
    private final MenuValidator validator;

    // 메뉴 전체 + 검색 조회
    public List<MenuListDto> menuList(String name, String code) {
        List<MenuListDto> menuList = menuMapper.findMenus(name,code);
        return menuList;
    }


    // 메뉴 생성
    public void createMenu(CreateMenuDto createMenuDto) {
        // 공통 검증
        validator.validateCode(createMenuDto.getCode());
        validator.validateCodeDup(createMenuDto.getCode());
        validator.validateLevel(createMenuDto.getLevel());

        UUID parentId = createMenuDto.getParentId();
        Integer newOrder = createMenuDto.getDisplayOrder();

        // 순서 자동
        if(newOrder == null || newOrder < 1){
            Integer max = menuMapper.findMaxOrder(parentId);
            newOrder = (max == null ? 1 : max + 1);
            createMenuDto.setDisplayOrder(newOrder);
        }

        processOrder(null,parentId,null,createMenuDto);
        //processOrder(createMenuDto.getParentId(), createMenuDto);
        menuMapper.menuInsert(createMenuDto);
    }

    // 하위 메뉴 생성
    public void createSubMenu(UUID parentId, CreateSubMenuDto createSubMenuDto){
        // 상위메뉴 여부 확인
        MenuListDto menuListDto = menuMapper.menuFindById(parentId);
        if(menuListDto == null){
            throw new IllegalArgumentException("상위 메뉴가 존재하지 않습니다.");
        }

        createSubMenuDto.setParentId(parentId);
        createSubMenuDto.setLevel(2);

        // 하위 메뉴 코드 검증
        validator.validateSubMenuCode(menuListDto.getCode(), createSubMenuDto.getCode());
        createSubMenuDto.setCode(menuListDto.getCode()+"_"+createSubMenuDto.getCode());



        Integer newOrder = createSubMenuDto.getDisplayOrder();

        // 순서 자동
        if(newOrder == null || newOrder < 1){
            Integer max = menuMapper.findMaxOrder(parentId);
            newOrder = (max == null ? 1 : max + 1);
            createSubMenuDto.setDisplayOrder(newOrder);
        }
       processOrder(null,parentId,null,createSubMenuDto);
         // processOrder(parentId,createSubMenuDto);

        menuMapper.menuSubInsert(createSubMenuDto);
    }

    // 메뉴 수정
    public void updateMenu(UUID id, UpdateMenuDto updateMenuDto) throws NotFoundException {

        MenuListDto menuListDto = menuMapper.menuFindById(id);
        if (menuListDto == null) {
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 코드 중복 체크 (code 필드를 수정할 때만)
        if (updateMenuDto.getCode() != null) {
            // code 유무 + 소문자
            validator.validateCode(updateMenuDto.getCode());
            // code 중복체크
            validator.validateCodeDup(updateMenuDto.getCode());
        }

        // 메뉴 순서 변경 시 자동
        if(updateMenuDto.getDisplayOrder() != null && !updateMenuDto.getDisplayOrder().equals(menuListDto.getDisplayOrder())){
            Integer newOrder = updateMenuDto.getDisplayOrder();
            Integer oldOrder = menuListDto.getDisplayOrder();
            UUID parentId = menuListDto.getParentId();

            processOrder(id,parentId,oldOrder,updateMenuDto);
        }
        menuMapper.menuUpdate(id, updateMenuDto);
    }

    // 메뉴삭제
    public void deleteMenu(UUID id) throws NotFoundException {

        if(menuMapper.countChildMenus(id) > 0)
            throw  new IllegalStateException("하위메뉴가 있어 삭제 불가합니다.");

        if(menuMapper.menuDelete(id) == 0){
            throw new NotFoundException("삭제할 메뉴가 존재하지 않습니다.");
        }
    }


    //메뉴 순서 변경
    public void changeMenu(UUID id , Integer displayOrder) {
        MenuListDto menuList = menuMapper.menuFindById(id);

        if(menuList == null ) {
            throw new IllegalArgumentException("메뉴를 찾을 수 없습니다.");
        }
        if(displayOrder == null || displayOrder < 1) {
            throw new IllegalArgumentException("순서는 1 이상이어야 합니다.");
        }

        menuMapper.shiftDisplayOrder(menuList.getParentId(), displayOrder);
        menuMapper.menuUpdateOrder(id,displayOrder);
    }

    // 메뉴 활성화 비활성화
    public void changeVisible(UUID id , Boolean visible) {
        if (visible == null){
            throw new IllegalStateException("true 또는 false 여야 합니다.");
        }
        menuMapper.menuUpdateVisible(id, visible);
    }

    // 메뉴 up
    public void moveUp(UUID id) throws NotFoundException{
        MenuListDto findId = menuMapper.menuFindById(id);
        if(findId == null)
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");

        if(findId.getDisplayOrder() == 1) // 1번이면 이동 불가
            return;

        // 현재 위에 있는 메뉴찾기
        MenuListDto upId = menuMapper.findByOrder(findId.getParentId(), findId.getDisplayOrder()-1);
        if(upId == null) return;

        int findIdOrder = findId.getDisplayOrder();
        int upIdOrder = upId.getDisplayOrder();

        // 메뉴 위로 올리고
        menuMapper.menuUpdateOrder(findId.getId(), upIdOrder);
        // 기존 메뉴
        menuMapper.menuUpdateOrder(upId.getId(),findIdOrder);
    }

    // 메뉴 down
    public void moveDown(UUID id) throws NotFoundException{
        MenuListDto findId = menuMapper.menuFindById(id);

        if(findId.getDisplayOrder() == null)
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");

        Integer maxOrder = menuMapper.findMaxOrder(findId.getParentId());
        if(findId.getDisplayOrder().equals(maxOrder))
            return;

        //현재 아래에 있는 메뉴 찾기
        MenuListDto lowId = menuMapper.findByOrder(findId.getParentId(),findId.getDisplayOrder()+1);
        if(lowId == null) return;

        int findIdOrder = findId.getDisplayOrder();
        int lowIdOrder = lowId.getDisplayOrder();

        menuMapper.menuUpdateOrder(findId.getId(), lowIdOrder);
        menuMapper.menuUpdateOrder(lowId.getId(),findIdOrder);
    }

    // 공통 메뉴 순서 자동정렬
    private void processOrder(UUID id, UUID parentId, Integer oldOrder, OrderUpdateble orderUpdateble){

        Integer newOrder = orderUpdateble.getDisplayOrder();
        if(newOrder == null || newOrder < 1) return;
        if(oldOrder != null && newOrder.equals(oldOrder)) return;

        // 메뉴 신규 생성인 경우
        if(oldOrder == null){
            menuMapper.shiftDisplayOrder(parentId,newOrder);
            return;
        }
        // 메뉴 수정 시
        if(newOrder < oldOrder){
            menuMapper.upMenu(parentId,newOrder, oldOrder -1);
        }else {
            menuMapper.downMenu(parentId, oldOrder +1, newOrder);
        }
        menuMapper.menuUpdateOrder(id,newOrder);
    }
}
