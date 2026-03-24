package kr.co.winnticket.menu.admmenu.service;

import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.mapper.AdminMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminMenuService {
    private final AdminMenuMapper mapper;

    // 관리자 메뉴 전체 + 검색 조회
    public List<AdminMenuListDto> searchAdminMenus(String title, String titleEn, String page) {
        return mapper.findAdminMenus(title, titleEn, page);
    }

    // 관리자 메뉴 생성
    @Transactional
    public void createAdmMenu(AdminMenuCreateDto dto) {

        validate(dto);

        int newOrder;
        // 순서 미지정시 마지막으로
        if (dto.getDisplayOrder() == null) {
            newOrder = mapper.admMenuGetMaxOrder() + 1;
        } else {
            if (dto.getDisplayOrder() < 1) {
                throw new IllegalStateException("순서는 1 이상");
            }
            newOrder = dto.getDisplayOrder();
            // 기존 데이터 밀기
            mapper.shiftUp(newOrder, mapper.admMenuGetMaxOrder());
        }

        dto.setDisplayOrder(newOrder);

        mapper.admMenuInsert(dto);
    }

    // 관리자 메뉴 수정
    @Transactional
    public void updateAdmMenu(UUID id, AdminMenuUpdateDto dto) {

        if (mapper.admMenuFindById(id) == null) {
            throw new IllegalArgumentException("관리자 메뉴를 찾을 수 없습니다.");
        }

        mapper.admMenuUpdate(id, dto);
    }


    // 관리자 메뉴 삭제
    public void deleteAdmMenu(UUID id) {

        if (mapper.admMenuFindById(id) == null) {
            throw new IllegalArgumentException("삭제할 관리자 메뉴가 존재하지 않습니다.");
        }

        mapper.admMenuDelete(id);
    }

    // 노출 순서 변경 (데드락 방지: 테이블 락 선점)
    @Transactional
    public void changeAdmMenu(UUID id, Integer newOrder) {

        // 데드락 방지: 모든 행을 일관된 순서로 잠금
        mapper.lockAllForUpdate();

        AdminMenuListDto menu = mapper.admMenuFindById(id);

        if (menu == null) throw new IllegalArgumentException("관리자메뉴가 없습니다.");

        if (newOrder == null || newOrder < 1)
            throw new IllegalStateException("관리자 메뉴 순서 오류입니다.");

        int oldOrder = menu.getDisplayOrder();

        if (newOrder.equals(oldOrder)) return;

        int max = mapper.admMenuGetMaxOrder();

        if (newOrder > max) newOrder = max;

        // 1) 자기 자신을 임시값(-1)으로 빼기
        mapper.admMenuUpdateOrder(id, -1);

        // 2) 범위 밀기
        if (newOrder < oldOrder) {
            mapper.shiftUp(newOrder, oldOrder);
        } else {
            mapper.shiftDown(oldOrder, newOrder);
        }

        // 3) 자기 자신을 새 위치에 넣기
        mapper.admMenuUpdateOrder(id, newOrder);
    }

    public void changeVisible(UUID id, Boolean visible) {

        if (visible == null) throw new IllegalStateException("관리자 메뉴 true/false 필요합니다.");

        if (mapper.admMenuFindById(id) == null)
            throw new IllegalArgumentException("관리자 메뉴가 없습니다.");

        mapper.admMenuUpdateVisible(id, visible);
    }

    // 생성 검증
    private void validate(AdminMenuCreateDto dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank())
            throw new IllegalStateException("메뉴명 필수");

        if (dto.getPage() == null || dto.getPage().isBlank())
            throw new IllegalStateException("page 필수");

        if (mapper.admMenuCountByPage(dto.getPage()) > 0)
            throw new IllegalStateException("페이지 중복");

        if (mapper.admMenuCountByTitle(dto.getTitle()) > 0)
            throw new IllegalStateException("메뉴명 중복");
    }

}
