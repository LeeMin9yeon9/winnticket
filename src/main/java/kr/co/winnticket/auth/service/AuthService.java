package kr.co.winnticket.auth.service;

import io.jsonwebtoken.Claims;
import kr.co.winnticket.auth.dto.*;
import kr.co.winnticket.auth.jwt.JwtTokenProvider;
import kr.co.winnticket.auth.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authmapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    private final FieldSessionService fieldSessionService;


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
        String sid = null;

        if ("ROLE002".equals(loginUser.getRoleId())) {
            sid = UUID.randomUUID().toString();
        }

        String roleId = loginUser.getRoleId();

        // ROLE002 계정 잠금 여부 확인
        if("ROLE002".equals(roleId)){
            if(loginAttemptService.isLocked(accountId,roleId)){
                throw new RuntimeException("계정이 잠겼습니다. 15분 후 다시 시도하세요.");
            }
        }

        // 비밀번호 검증
        boolean passwordMatches;
        if ("ROLE001".equals(roleId)) {
            // 직원(ROLE001) → bcrypt
            passwordMatches = passwordEncoder.matches(password,loginUser.getPassword());
        } else if ("ROLE002".equals(roleId)) {
            // 현장 관리자(ROLE002) → 평문
            passwordMatches = password.equals(loginUser.getPassword());
        } else {
            throw new RuntimeException("Unknown role: " + roleId);
        }

        // 비밀번호 틀렸을 때
        if(!passwordMatches){
            loginAttemptService.recordFailedAttempt(accountId,roleId);
            throw new RuntimeException("패스워드가 틀렸습니다.");
        }

        // 비밀번호가 맞으면 실패 카운트 초기화
        loginAttemptService.resetFailCount(accountId,roleId);


        // 로그인 마지막 시간 업데이트
        if("ROLE002".equals(loginUser.getRoleId())){
            authmapper.updateLastLoginAt(loginUser.getId());
        }

        // JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(
                loginUser.getId(),
                loginUser.getName(),
                loginUser.getAccountId(),
                loginUser.getRoleId(),
                loginUser.getUserType(),
                loginUser.getPartnerId(),
                sid
        );
        String refreshToken = null;
        if("ROLE002".equals(loginUser.getRoleId())) {

            refreshToken = jwtTokenProvider.createRefreshToken(
                    loginUser.getId(),
                    loginUser.getAccountId(),
                    loginUser.getRoleId(),
                    sid
            );
            long refreshTtl = jwtTokenProvider.getExpiration(refreshToken) - System.currentTimeMillis();
            refreshTokenService.refreshStore(loginUser.getAccountId(),refreshToken,refreshTtl);
            fieldSessionService.store(loginUser.getAccountId(), sid, refreshTtl);
        }
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


    public void logout(String accessToken){
        if (accessToken == null) {
            return; // 토큰 없어도 로그아웃 성공 처리
        }

        // Access Token 블랙리스트
        tokenBlacklistService.blacklistAccessToken(accessToken);

        try {
            // Access Token에서 사용자 식별
            Claims claims = jwtTokenProvider.getClaimsAllowExpired(accessToken);
            String accountId = claims.get("accountId", String.class);
            String role = claims.get("roleId", String.class);



            // ROLE002만 Refresh Token 삭제
            if ("ROLE002".equals(role)) {
                refreshTokenService.refreshDelete(accountId);
                fieldSessionService.delete(accountId);
            }
        }catch(Exception e){

        }
    }


    public TokenResponseDto refresh(RefreshTokenRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        // 토큰 유효성 검증
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 블랙리스트 여부
        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw new RuntimeException("Token blacklisted");
        }

        // Claims 추출
        var claims = jwtTokenProvider.getClaims(refreshToken);

        String userId   = claims.getSubject();
        String accountId = claims.get("accountId", String.class);
        String roleId    = claims.get("roleId", String.class);
        String type      = claims.get("type", String.class);
        String sid = claims.get("sid", String.class);

        if (sid == null) {
            throw new RuntimeException("Session expired. Please login again.");
        }

        String currentSid = fieldSessionService.get(accountId);
        if (currentSid == null || !currentSid.equals(sid)) {
            throw new RuntimeException("다른 기기에서 로그인되었습니다.");
        }

        // Refresh 타입, ROLE002(현장관리자)만 허용
        if (!"refresh".equals(type)) {
            throw new RuntimeException("Invalid token type");
        }
        if (!"ROLE002".equals(roleId)) {
            throw new RuntimeException("현장관리자만 Refresh Token을 사용할 수 있습니다.");
        }

        // Redis에 저장된 Refresh Token과 비교
        String savedRefresh = refreshTokenService.refreshGet(accountId);
        if (savedRefresh == null || !savedRefresh.equals(refreshToken)) {
            throw new RuntimeException("Refresh token mismatch");
        }

        // 기존 Refresh Token 블랙리스트 + 삭제 (rotate)
        tokenBlacklistService.blacklistRefreshToken(refreshToken);
        refreshTokenService.refreshDelete(accountId);

        // 유저 정보 다시 조회 (현장관리자 테이블에서)
        LoginUserDbDto user = authmapper.selectFieldAccountId(accountId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        //  새 Access / Refresh 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getName(),
                user.getAccountId(),
                user.getRoleId(),
                user.getUserType(),
                user.getPartnerId(),
                sid
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(
                user.getId(),
                user.getAccountId(),
                user.getRoleId(),
                sid
        );

        long refreshTtl = jwtTokenProvider.getExpiration(newRefreshToken) - System.currentTimeMillis();
        refreshTokenService.refreshStore(user.getAccountId(), newRefreshToken, refreshTtl);
        // 세션 TTL 갱신
        fieldSessionService.store(user.getAccountId(), sid, refreshTtl);

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }



}
