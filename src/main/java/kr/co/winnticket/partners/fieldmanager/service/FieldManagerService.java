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
    public FieldManagerResDto getDetail(UUID partnerId,UUID id) {
        return mapper.getDetail(id,partnerId);
    }

    // 현장관리자 추가
    public FieldManagerResDto create(UUID partnerId,FieldManagerInsertPostDto dto){

        if (mapper.existsByAccountId(dto.getUserName())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }

        UUID id = UUID.randomUUID();
        dto.setPartnerId((partnerId));

        mapper.insert(id,dto);

        return mapper.getDetail(id, dto.getPartnerId());
    }

    // 현장관리자 수정
    public FieldManagerResDto update(UUID partnerId, UUID id, UpdateFieldManagerDto model){

        if (mapper.existsByAccountIdExcludeId(model.getUserName(),id)) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }

        mapper.update(partnerId,id,model);

        return mapper.getDetail(id,partnerId);
    }

    // 현장관리자 본인 변경
    public void changePassword(UUID partnerId, UUID id , ChangePasswordDto model){
        if(model.getNewPassword() == null || model.getNewPassword().isBlank()){
            throw new IllegalArgumentException("새 비밀번호가 필요합니다.");
        }

        FieldManagerResDto manager = mapper.getDetail(id, partnerId);

        if(manager == null){
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        String currentPw = mapper.getPassword(partnerId,id);

        if(!currentPw.trim().equals(model.getCurrentPassword().trim())){
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }
        mapper.updatePassword(partnerId,id,model.getNewPassword().trim());
    }


    // 현장관리자 PW 초기화
    public void resetPassword(UUID partnerId,UUID id, ResetPasswordDto model){
        mapper.resetPassword(partnerId,id,model.getNewPassword());
    }

    // 현장관리자 삭제
    public void delete(UUID partnerId, UUID id){

        FieldManagerResDto manager = mapper.getDetail(id, partnerId);

        if(manager == null){
            throw new IllegalArgumentException("존재하지 않는 관리자입니다.");
        }

        mapper.delete(partnerId,id);
    }



}
