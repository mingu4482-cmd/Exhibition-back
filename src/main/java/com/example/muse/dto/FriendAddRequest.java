package com.example.muse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FriendAddRequest {
    @NotBlank
    @Email
    private String email; // 친구 이메일
}