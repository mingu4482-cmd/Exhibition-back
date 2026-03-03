package com.example.muse.controller;

import com.example.muse.dto.LoginRequest;
import com.example.muse.dto.SignupRequest;
import com.example.muse.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ✅ 회원가입 (프론트는 response.text()로 읽어도 OK)
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return "OK";
    }

    // ✅ 로그인: 이메일/아이디 둘 다 가능 (LoginRequest의 login 필드 사용)
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req.getLogin(), req.getPassword());
    }

    // ✅ 아이디 중복확인: GET /api/auth/check-id?loginId=test1
    @GetMapping("/check-id")
    public Map<String, Object> checkId(@RequestParam String loginId) {
        boolean available = authService.isLoginIdAvailable(loginId);
        return Map.of("isAvailable", available);
    }
}