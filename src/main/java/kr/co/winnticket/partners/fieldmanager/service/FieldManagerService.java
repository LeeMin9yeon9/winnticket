package kr.co.winnticket.partners.fieldmanager.service;

import kr.co.winnticket.partners.fieldmanager.dto.*;
import kr.co.winnticket.partners.fieldmanager.mapper.FieldManagerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldManagerService {

    private final FieldManagerMapper mapper;
    private final PasswordEncoder passwordEncoder;

    private static boolean isBcryptHash(String s) {
        return s != null && (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$"));
    }

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

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호가 필요합니다.");
        }
        // BCrypt 해시 후 저장 (이미 해시면 그대로)
        if (!isBcryptHash(dto.getPassword())) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
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
        String input = model.getCurrentPassword() == null ? "" : model.getCurrentPassword().trim();

        boolean currentMatches;
        if (isBcryptHash(currentPw)) {
            currentMatches = passwordEncoder.matches(input, currentPw);
        } else {
            currentMatches = currentPw != null && currentPw.trim().equals(input);
        }
        if (!currentMatches) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        String hashed = passwordEncoder.encode(model.getNewPassword().trim());
        mapper.updatePassword(partnerId, id, hashed);
    }


    // 현장관리자 PW 초기화
    public void resetPassword(UUID partnerId,UUID id, ResetPasswordDto model){
        if (model.getNewPassword() == null || model.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("새 비밀번호가 필요합니다.");
        }
        String hashed = passwordEncoder.encode(model.getNewPassword());
        mapper.resetPassword(partnerId, id, hashed);
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
