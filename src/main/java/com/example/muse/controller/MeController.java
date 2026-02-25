package com.example.muse.controller;

import com.example.muse.dto.MeResponse;
import com.example.muse.dto.UpdateNicknameRequest;
import com.example.muse.service.MeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class MeController {

    private final MeService meService;

    @GetMapping
    public MeResponse me(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return meService.getMe(email);
    }

    // ✅ 닉네임 수정
    @PatchMapping("/nickname")
    public Map<String, Object> updateNickname(@RequestBody @Valid UpdateNicknameRequest req,
                                              Authentication auth) {
        String email = (String) auth.getPrincipal();
        String updated = meService.updateNickname(email, req.getNickname());
        return Map.of("ok", true, "nickname", updated);
    }
}