package kr.co.winnticket.menu.common;

import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.mapper.MenuCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MenuValidator {
    private final MenuCategoryMapper mapper;

    // code 기본 + 소문자 정규식
    public void validateCode(String code){
        if(code == null || code.isBlank()){
            throw new IllegalArgumentException("메뉴 코드는 필수입니다.");
        }
        if(!code.matches("^[a-z]+$")){
            throw new IllegalArgumentException("메뉴 코드는 영문 소문자(a-z)만 가능합니다.");
        }
    }
    // code 중복 체크
    public void validateCodeDup(String code, UUID id){
        if(mapper.existsByCodeAndNotId(code, id) > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 코드입니다.");
        }
    }

    // level 검증
    public void validateLevel(Integer level){
        if(level == null || (level != 1 && level != 2)){
            throw new IllegalArgumentException("레벨은 1 또는 2 여야 합니다.");
        }
    }

    // parentId 검증
    public void validateParent(Integer level, UUID parentId) {

        // 상위메뉴
        if (level == 1) {
            if (parentId != null) {
                throw new IllegalArgumentException("상위 메뉴는 parentId가 존재하지 않습니다.");
            }
            return;
        }
        // 하위메뉴
        MenuListDto parent = mapper.menuFindById(parentId);
        if (parent == null) {
            throw new IllegalArgumentException("하위 메뉴는 parentId가 필수입니다.");
        }
            if (parent.getLevel() != 1) {
                throw new IllegalArgumentException("하위 메뉴는 level = 2 입니다.");
            }

    }

    // 하위 메뉴 코드 검증 (부모 + 자식 조합 후 중복 체크)
    public void validateSubMenuCode(String parentCode, String childCode) {
        if (parentCode == null || parentCode.isBlank()) {
            throw new IllegalArgumentException("부모 메뉴 코드가 유효하지 않습니다.");
        }

        // 소문자 검증
        validateCode(childCode);

        String finalCode = parentCode + "_" + childCode;
        
        if (mapper.existsByCode(finalCode) > 0) {
            throw new IllegalArgumentException("이미 존재하는 최종 메뉴 코드입니다.");
        }
    }




}
