package kr.co.winnticket.menu.admmenu.service;

import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuSearchDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.entity.AdminMenus;
import kr.co.winnticket.menu.admmenu.mapper.AdminMenuMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMenuService {
    private final AdminMenuMapper adminMapper;

    //관리자 메뉴 전체 리스트 조회
    public List<AdminMenuListDto> getAllList(){
        List<AdminMenus> list = adminMapper.admMenuAllList();
        return list.stream()
                .map(m->AdminMenuListDto.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .titleEn(m.getTitleEn())
                        .icon(m.getIcon())
                        .page(m.getPage())
                        .displayOrder(m.getDisplayOrder())
                        .visible(m.getVisible())
                        .build())
                .toList();
    }

    // 관리자 메뉴 생성
    public AdminMenus createAdmMenu(AdminMenuCreateDto createDto) {

        // 메뉴명 필수 값 체크
        if (createDto.getTitle() == null || createDto.getTitle().isEmpty()) {
            throw new IllegalStateException("메뉴 이름은 필수 값 입니다.");
        }
        // 페이지 중복 체크
        if (adminMapper.admMenuCountByPage(createDto.getPage()) > 0) {
            throw new IllegalStateException("이미 존재하는 페이지입니다 :" + createDto.getPage());
        }
        // 메뉴명 중복 체크
        if (adminMapper.admMenuCountByTitle(createDto.getTitle()) > 0) {
            throw new IllegalStateException("이미 존재하는 메뉴명입니다 :" + createDto.getTitle());
        }

        // 순서 자동 설정
        Integer displayOrder = createDto.getDisplayOrder();
        int nextOrder = 0;
        if (displayOrder == null) {
            int maxOrder = adminMapper.admMenuGetMaxOrder();
            nextOrder = maxOrder + 1;
        } else {
            if (displayOrder < 1) {
                throw new IllegalStateException("표시 순서는 1 이상이어야 합니다.");
            }
        }

        UUID admId = UUID.randomUUID();

        AdminMenus adminMenu = AdminMenus.builder()
                .id(admId)
                .title(createDto.getTitle())
                .titleEn(createDto.getTitleEn())
                .icon(createDto.getIcon())
                .page(createDto.getPage())
                .displayOrder(
                        createDto.getDisplayOrder() != null ?
                                createDto.getDisplayOrder() : nextOrder
                )
                .visible(createDto.getVisible() != null ? createDto.getVisible() : false)
                .build();
        adminMapper.admMenuInsert(adminMenu);


        return adminMenu;
    }

    // 관리자 메뉴 수정
    public boolean updateAdmMenu(AdminMenuUpdateDto updateDto) throws NotFoundException {

        // 기존 메뉴 조회
        AdminMenus adminMenus = adminMapper.admMenuFindById(UUID.fromString(updateDto.getId()));
        if (adminMenus == null) {
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 변경된 항목만 저장
        boolean changed = false;

        if (updateDto.getTitle() != null && !updateDto.getTitle().equals(adminMenus.getTitle())) {
            adminMenus.setTitle(updateDto.getTitle());
            changed = true;
        }
        if (updateDto.getTitleEn() != null && !updateDto.getTitleEn().equals(adminMenus.getTitleEn())) {
            adminMenus.setTitleEn(updateDto.getTitleEn());
            changed = true;
        }
        if (updateDto.getIcon() != null && !updateDto.getIcon().equals(adminMenus.getIcon())) {
            adminMenus.setIcon(updateDto.getIcon());
            changed = true;
        }
        if (updateDto.getPage() != null && !updateDto.getPage().equals(adminMenus.getPage())) {
            adminMenus.setPage(updateDto.getPage());
            changed = true;
        }
        if (updateDto.getDisplayOrder() != null && !updateDto.getDisplayOrder().equals(adminMenus.getDisplayOrder())) {
            adminMenus.setDisplayOrder(updateDto.getDisplayOrder());
            changed = true;
        }
        if (updateDto.getVisible() != null && !updateDto.getVisible().equals(adminMenus.getVisible())) {
            adminMenus.setVisible(updateDto.getVisible());
            changed = true;
        }

        if (!changed) {
            return false; // 변경 없음
        }

        adminMapper.admMenuUpdate(adminMenus);
        return true; // 정상 업데이트 완료
    }

    // 관리자 메뉴 삭제
    public void deleteAdmMenu(UUID id) throws NotFoundException {
        AdminMenus adminMenus = adminMapper.admMenuFindById(id);
        if(adminMenus == null){
            throw new NotFoundException("삭제할 메뉴가 존재하지 않습니다.");
        }
        adminMapper.admMenuDelete(id);
    }

    // 관리자 메뉴 검색

    public List<AdminMenuListDto> searchAdmMenu(AdminMenuSearchDto searchDto) {

        List<AdminMenus> adminMenus = adminMapper.admMenuSearch(searchDto);
        return adminMenus.stream()
                .map(m -> AdminMenuListDto.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .titleEn(m.getTitleEn())
                        .icon(m.getIcon())
                        .page(m.getPage())
                        .displayOrder(m.getDisplayOrder())
                        .visible(m.getVisible())
                        .build()
                ).toList();
    }

    // 노출 순서 변경
    public void changeAdmMenu(UUID id , Integer order) throws NotFoundException {
        AdminMenus adminMenus = adminMapper.admMenuFindById(id);
        if(adminMenus == null){
            throw new NotFoundException("변경할 메뉴가 존재하지 않습니다.");
        }
        if (order == null || order < 1){
            throw new IllegalStateException("노출 순서는 1 이상의 값이여야 합니다.");
        }
        adminMapper.admMenuUpdateOrder(id,order);
    }

    // 메뉴 활성화 비활성화
    public void changeVisible(UUID id, Boolean visible) throws NotFoundException{

        AdminMenus adminMenus = adminMapper.admMenuFindById(id);
        if(adminMenus == null){
            throw new NotFoundException("변경할 메뉴가 존재하지 않습니다.");
        }
        if (visible == null){
            throw new IllegalStateException("true 또는 false 여야 합니다.");
        }
        adminMapper.admMenuUpdateVisible(id, visible);
    }
}
