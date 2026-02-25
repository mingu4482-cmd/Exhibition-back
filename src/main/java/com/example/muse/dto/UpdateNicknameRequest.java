package com.example.muse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateNicknameRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    private String nickname;

    public UpdateNicknameRequest() {}

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}