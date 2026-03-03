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

    // ✅ 회원가입
    @PostMapping("/signup")
    public Map<String, Object> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return Map.of("ok", true);
    }

    // ✅ 로그인 (토큰 발급)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return Map.of("token", token);
    }

    // ✅ 아이디(로그인ID) 중복확인
    // GET /api/auth/check-id?loginId=test1
    @GetMapping("/check-id")
    public Map<String, Object> checkId(@RequestParam String loginId) {
        boolean available = authService.isLoginIdAvailable(loginId);
        return Map.of("isAvailable", available);
    }
}