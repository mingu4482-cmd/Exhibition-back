package com.example.muse.dto;

public record MeResponse(
        Long id,
        String email,
        String loginId,
        String nickname,
        String role
) {}