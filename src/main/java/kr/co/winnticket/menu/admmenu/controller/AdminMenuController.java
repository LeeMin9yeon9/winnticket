package kr.co.winnticket.menu.admmenu.controller;

import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuSearchDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
//@RequestMapping("/api/admin/menus")
//@RequestMapping("/api/v1/adminmenulist")
@RequiredArgsConstructor
public class AdminMenuController {

    private final AdminMenuService adminMenuService;


     // 관리자 메뉴 전체 리스트 조회

    @GetMapping("/api/menu/adminMenuList")
    public List<AdminMenuListDto> getAllAdmMenus() {
        return adminMenuService.getAllList();
    }

    // 관리자 메뉴 생성
    @PostMapping("/api/menu/adminMenuInsert")
    public String createAdmMenu(@RequestBody AdminMenuCreateDto createDto) {
        adminMenuService.createAdmMenu(createDto);
        return "OK";

    }

    // 관리자 메뉴 수정
    @PostMapping("/api/menu/adminMenuUpdate")
    public String updateAdmMenu(@RequestBody AdminMenuUpdateDto updateDto) throws NotFoundException {
        adminMenuService.updateAdmMenu(updateDto);
        return "OK";
    }

    //관리자 메뉴 삭제

    @PostMapping("/api/menu/adminMenuDelete")
    public String deleteAdmMenu(@RequestParam UUID id) throws NotFoundException {
        adminMenuService.deleteAdmMenu(id);
        return "OK";
    }

    // 관리자 메뉴 조회
    @PostMapping("/api/menu/adminMenuSearch")
    public List<AdminMenuListDto> searchAdmMenu(@RequestBody AdminMenuSearchDto searchDto){
        return adminMenuService.searchAdmMenu(searchDto);
    }

    // 메뉴 노출 순서 변경
    @PostMapping("/api/menu/adminMenuOrder")
    public String changeAdmOrder(
            @RequestParam UUID id,
            @RequestParam Integer order
    ) throws NotFoundException {
        adminMenuService.changeAdmMenu(id, order);
        return "OK";
    }

    // 메뉴 활성/비활성 변경
    @PostMapping("/api/menu/adminMenuVisible")
    public String changeAdmVisible(
            @RequestParam UUID id,
            @RequestParam Boolean visible
    ) throws NotFoundException {
        adminMenuService.changeVisible(id, visible);
        return "OK";
    }
}
