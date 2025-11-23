package kr.co.winnticket.menu.admmenu.service;

import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.mapper.AdminMenuMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMenuService {
    private final AdminMenuMapper adminMapper;

    // 관리자 메뉴 전체 + 검색 조회
    public List<AdminMenuListDto> searchAdminMenus(String title, String titleEn, String page) {
        return adminMapper.findAdminMenus(title,titleEn,page);
    }

    // 관리자 메뉴 생성
    @Transactional
    public void createAdmMenu(AdminMenuCreateDto createDto) {
        if(createDto.getDisplayOrder() == null){
            int maxOrder = adminMapper.admMenuGetMaxOrder();
            createDto.setDisplayOrder(maxOrder+1);
        }else {
            adminMapper.shiftUp(createDto.getDisplayOrder(), adminMapper.admMenuGetMaxOrder());
        }
        adminMapper.admMenuInsert(createDto);
    }

    // 관리자 메뉴 수정

    @Transactional
    public boolean updateAdmMenu(UUID id, AdminMenuUpdateDto updateDto) throws NotFoundException {
        AdminMenuListDto dto = adminMapper.admMenuFindById(id);
        if (dto == null) {
            throw new NotFoundException("메뉴를 찾을 수 없습니다.");
        }

        // 순서 변경은 여기서 처리하지 않음
        adminMapper.admMenuUpdate(id, updateDto);
        return true;
    }


    // 관리자 메뉴 삭제
    public void deleteAdmMenu(UUID id) throws NotFoundException {
         AdminMenuListDto adminMenuListDto = adminMapper.admMenuFindById(id);
        if (adminMenuListDto == null) {
            throw new NotFoundException("삭제할 메뉴가 존재하지 않습니다.");
        }
        adminMapper.admMenuDelete(id);
    }

    // 노출 순서 변경
    public void changeAdmMenu(UUID id, Integer newOrder) throws NotFoundException{
        AdminMenuListDto adminMenuListDto = adminMapper.admMenuFindById(id);
        if (newOrder == null || newOrder < 1) {
            throw new IllegalStateException("표시 순서는 1 이상의 값이어야 합니다.");
        }
        Integer oldOrder = adminMenuListDto.getDisplayOrder();

        if (newOrder.equals(oldOrder)) return;

        Integer max = adminMapper.admMenuGetMaxOrder();
        if(newOrder > max) newOrder = max;

        if(newOrder < oldOrder){
            adminMapper.shiftUp(newOrder,oldOrder);
        }else {
            adminMapper.shiftDown(oldOrder , newOrder);
        }
        adminMapper.admMenuUpdateOrder(id , newOrder);

    }

    // 메뉴 활성화 비활성화
    public void changeVisible(UUID id, Boolean visible) {
        if (visible == null) {
            throw new IllegalStateException("visible 값은 true 또는 false 여야 합니다.");
        }
        adminMapper.admMenuUpdateVisible(id, visible);
    }


    // 생성시 검증 및 순번 계산
    private void validateMenuCreate(AdminMenuCreateDto createDto) {
        if (createDto.getTitle() == null || createDto.getTitle().isEmpty()) {
            throw new IllegalStateException("메뉴 이름은 필수 값 입니다.");
        }
        if (adminMapper.admMenuCountByPage(createDto.getPage()) > 0) {
            throw new IllegalStateException("이미 존재하는 페이지입니다: " + createDto.getPage());
        }
        if (adminMapper.admMenuCountByTitle(createDto.getTitle()) > 0) {
            throw new IllegalStateException("이미 존재하는 메뉴명입니다: " + createDto.getTitle());
        }
    }

    // 표시순서 검증
    private int getNextDisplayOrder(Integer inputOrder) {
        if (inputOrder == null) {
            return adminMapper.admMenuGetMaxOrder() + 1;
        }
        if (inputOrder < 1) {
            throw new IllegalStateException("표시 순서는 1 이상이어야 합니다.");
        }
        return inputOrder;
    }




}
