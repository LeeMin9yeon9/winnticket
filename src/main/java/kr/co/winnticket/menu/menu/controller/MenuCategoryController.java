package kr.co.winnticket.menu.menu.controller;

import kr.co.winnticket.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.dto.UpdateMenuDto;
import kr.co.winnticket.menu.menu.entity.MenuCategory;
import kr.co.winnticket.menu.menu.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MenuCategoryController {

    private final MenuCategoryService menuService;

    // 전체메뉴 조회
    @GetMapping("/api/menu/menuListAll")
    public List<MenuListDto> menuAllList(){
        return menuService.MenuGetAllList();
    }

    // 메뉴 생성
    @PostMapping("/api/menu/menuInsert")
    public MenuCategory menuCreate(@RequestBody CreateMenuDto createMenuDto) {
        return menuService.MenuCreate(createMenuDto);
    }

    // 메뉴 수정
    @PatchMapping("/api/menu/menuUpdate")
    public String menuUpdate(@RequestBody UpdateMenuDto updateMenuDto) throws NotFoundException {
        menuService.updateMenu(updateMenuDto);
        return "OK";
    }

    // 메뉴 삭제
    @DeleteMapping("/api/menu/menuDelete")
    public String menuDelete(@PathVariable UUID id) throws NotFoundException {
        menuService.deleteMenu(id);
        return "OK";
    }


}
