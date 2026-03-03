package com.example.muse.controller;

import com.example.muse.dto.LoginRequest;
import com.example.muse.dto.SignupRequest;
import com.example.muse.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ✅ 회원가입: 프론트가 text로 받아도 OK
    @PostMapping(value = "/signup", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return ResponseEntity.ok("OK");
    }

    // ✅ 로그인: 프론트가 response.text()로 "토큰 문자열"만 받게
    @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(token);
    }

    // ✅ 아이디 중복확인: 이미 프론트랑 맞음
    @GetMapping("/check-id")
    public Map<String, Object> checkId(@RequestParam String loginId) {
        boolean available = authService.isLoginIdAvailable(loginId);
        return Map.of("isAvailable", available);
    }
}