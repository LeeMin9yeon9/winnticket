//package kr.co.winnticket.auth.controller;
//
//import kr.co.winnticket.auth.dto.LoginRequestDto;
//import kr.co.winnticket.auth.dto.LoginResponseDto;
//import kr.co.winnticket.auth.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/api/auth")
//public class AuthController {
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDto> login(
//            @RequestBody LoginRequestDto loginRequestDto){
//        return ResponseEntity.ok(authService.login(loginRequestDto));
//    }
//}
