package kr.co.winnticket.menu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.menu.menu.dto.CreateSubMenuDto;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.dto.UpdateMenuDto;
import kr.co.winnticket.menu.menu.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "쇼핑몰 메뉴 관리", description = "쇼핑몰 메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu/menuCategory")
public class MenuCategoryController {

    private final MenuCategoryService menuService;

//    @GetMapping({"/", "/name/{name}/code/{code}", "/name/{name}", "/code/{code}"})
//    @Operation(summary = "쇼핑몰 메뉴 목록 조회" , description = "쇼핑몰 메뉴 목록을 검색하여 조회합니다.")
//    public ResponseEntity<List<MenuListDto>> getAllMenus(
//        @Parameter(description = "메뉴명") @RequestParam(required = false) String name,
//        @Parameter(description = "메뉴코드") @RequestParam(required = false) String code
//        ){
//        List<MenuListDto> menus = menuService.searchMenus(name,code);
//        return ResponseEntity.ok(menus);
//    }
    @GetMapping
    @Operation(summary = "쇼핑몰 메뉴 전체 조회")
    public ResponseEntity<List<MenuListDto>> getAllMenus() {
        return ResponseEntity.ok(menuService.searchMenus(null, null));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "메뉴명 검색")
    public ResponseEntity<List<MenuListDto>> getByName(@PathVariable String name) {
        return ResponseEntity.ok(menuService.searchMenus(name, null));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "메뉴코드 검색")
    public ResponseEntity<List<MenuListDto>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(menuService.searchMenus(null, code));
    }

    @GetMapping("/name/{name}/code/{code}")
    @Operation(summary = "메뉴명 + 메뉴코드 검색")
    public ResponseEntity<List<MenuListDto>> getByNameAndCode(
            @PathVariable String name,
            @PathVariable String code
    ) {
        return ResponseEntity.ok(menuService.searchMenus(name, code));
    }
    @PostMapping
    @Operation(summary = "쇼핑몰 메뉴 생성" , description = "새로운 메뉴를 생성합니다.")
    public ResponseEntity<String> createMenu(@RequestBody CreateMenuDto createMenuDto) {
        menuService.createMenu(createMenuDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    @PostMapping("/sub/{parentId}")
    @Operation(summary = "쇼핑몰 하위 메뉴 생성", description = "새로운 하위 메뉴를 생성합니다.")
    private ResponseEntity<?> createSubMenu(
            @PathVariable UUID parentId,
            @RequestBody CreateSubMenuDto createSubMenuDto
    ){
        menuService.createSubMenu(parentId,createSubMenuDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }


    @PatchMapping("/{id}")
    @Operation(summary = "쇼핑몰 메뉴 수정" , description = "쇼핑몰 메뉴 정보를 수정합니다.")
    public ResponseEntity<String> updateMenu(
            @PathVariable UUID id,
            @RequestBody UpdateMenuDto updateMenuDto
    ) throws NotFoundException{
         menuService.updateMenu(id,updateMenuDto);
        return ResponseEntity.ok("Updated");
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "쇼핑몰 메뉴 삭제" , description = "쇼핑몰 메뉴를 삭제합니다.")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID id) throws NotFoundException {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/displayOrder/{id}/{displayOrder}")
    @Operation(summary = "쇼핑몰 메뉴 순서 변경" , description = "쇼핑몰 메뉴의 노출 순서를 변경합니다.")
    public ResponseEntity<String> changeMenuOrder(
            @PathVariable UUID id,
            @PathVariable Integer displayOrder
    ) throws NotFoundException{
        menuService.changeMenu(id,displayOrder);
        return ResponseEntity.ok("order Updated");
    }

    @PatchMapping("/displayOrder/up/{id}")
    @Operation(summary = "쇼핑몰 메뉴 순서를 위로 이동" , description = "쇼핑몰 메뉴 순서를 위로 이동합니다.")
    public ResponseEntity<String> moveUp(
            @PathVariable UUID id
    ) throws NotFoundException{
        menuService.moveUp(id);
        return ResponseEntity.ok("moved up");
    }

    @PatchMapping("/displayOrder/down/{id}")
    @Operation(summary = "쇼핑몰 메뉴 순서를 아래로 이동" ,description = "쇼핑몰 메뉴 순서를 아래로 이동합니다.")
    public ResponseEntity<String> moveDown(
            @PathVariable UUID id
    )throws NotFoundException{
        menuService.moveDown(id);
        return ResponseEntity.ok("moved down");
    }

    @PatchMapping("/visible/{id}/{visible}")
    @Operation(summary = "쇼핑몰 메뉴 활성화/비활성화" , description = "쇼핑몰 메뉴를 활성/비활성화 합니다.")
    public ResponseEntity<String> visibleMenu(
            @PathVariable UUID id,
            @PathVariable Boolean visible
    ) throws NotFoundException{
        menuService.changeVisible(id , visible);
        return ResponseEntity.ok("visible updated");
    }

}
