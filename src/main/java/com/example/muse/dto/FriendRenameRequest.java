package com.example.muse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FriendRenameRequest {
    @NotBlank
    private String friendName; // 친구 별명/이름
}