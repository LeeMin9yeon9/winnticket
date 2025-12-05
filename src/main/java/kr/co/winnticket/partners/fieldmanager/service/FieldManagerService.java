package kr.co.winnticket.partners.fieldmanager.service;

import kr.co.winnticket.partners.fieldmanager.dto.*;
import kr.co.winnticket.partners.fieldmanager.mapper.FieldManagerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldManagerService {

    private final FieldManagerMapper mapper;

    // 현장관리자 목록
    public List<FieldManagerListGetResDto> getListByPartner(String partnerId){
        return mapper.getListByPartner(partnerId);
    }

    // 현장관리자 상세조회
    public FieldManagerResDto getDetail(UUID id) {
        return mapper.getDetail(id);
    }

    // 현장관리자 추가
    public FieldManagerResDto create(FieldManagerInsertPostDto model){
        UUID id = UUID.randomUUID();

        mapper.insert(id,model);

        return mapper.getDetail(id);
    }

    // 현장관리자 수정
    public FieldManagerResDto update(UUID id, UpdateFieldManagerDto model){
        mapper.update(id,model);

        return mapper.getDetail(id);
    }

    // 현장관리자 본인 변경
    public void changePassword(UUID id , ChangePasswordDto model){
        if(model.getNewPassword() == null || model.getNewPassword().isBlank()){
            throw new IllegalArgumentException("새 비밀번호가 필요합니다.");
        }
        String currentPw = mapper.getPassword(id);

        if(!currentPw.trim().equals(model.getCurrentPassword().trim())){
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        mapper.updatePassword(id,model.getNewPassword().trim());
    }


    // 현장관리자 PW 초기화
    public void resetPassword(UUID id, ResetPasswordDto model){
        mapper.updatePassword(id,model.getNewPassword());
    }

    // 현장관리자 삭제
    public void delete(UUID id){
        mapper.delete(id.toString());
    }


}
