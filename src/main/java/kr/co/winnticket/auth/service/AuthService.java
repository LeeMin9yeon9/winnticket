package kr.co.winnticket.auth.service;

import kr.co.winnticket.auth.dto.*;
import kr.co.winnticket.auth.jwt.JwtTokenProvider;
import kr.co.winnticket.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authmapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        String accountId = loginRequestDto.getAccountId();
        String password = loginRequestDto.getPassword();

        // 직원 조회
        LoginUserDbDto loginUser = authmapper.selectEmpAccountId(accountId);

        // 직원 없으면 현장 관리자 조회
        if(loginUser == null){
            loginUser = authmapper.selectFieldAccountId(accountId);
        }
        if(loginUser == null){
            throw new RuntimeException("Account not found");
        }

        String roleId = loginUser.getRoleId();

        if ("ROLE001".equals(roleId)) {
            // 직원(ROLE001) → bcrypt
            if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                throw new RuntimeException("Incorrect password");
            }

        } else if ("ROLE002".equals(roleId)) {
            // 현장 관리자(ROLE002) → 평문
            if (!password.equals(loginUser.getPassword())) {
                throw new RuntimeException("Incorrect password");
            }

        } else {
            throw new RuntimeException("Unknown role: " + roleId);
        }
        authmapper.updateLastLoginAt(loginUser.getId());


        // JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                loginUser.getId(),
                loginUser.getName(),
                loginUser.getAccountId(),
                loginUser.getRoleId(),
                loginUser.getUserType(),
                loginUser.getPartnerId()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(loginUser.getId());

        // JWT 응답
        AuthUserDto authUser = AuthUserDto.builder()
                .id(loginUser.getId())
                .name(loginUser.getName())
                .accountId(loginUser.getAccountId())
                .roleId(loginUser.getRoleId())
                .avatarUrl(loginUser.getAvatarUrl())
                .userType(loginUser.getUserType())
                .partnerId(loginUser.getPartnerId())
                .build();

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(authUser)
                .build();

    }
    public void logout(String accessToken, LogoutRequestDto logoutRequestDto){

    }


}
