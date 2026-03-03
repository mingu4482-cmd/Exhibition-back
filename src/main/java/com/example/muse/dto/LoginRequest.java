package com.example.muse.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "이메일 또는 아이디를 입력해주세요.")
    @JsonAlias({"email", "loginId", "id", "login"})
    private String login;  // ✅ email/아이디 어떤 키로 와도 여기에 들어감

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    public LoginRequest() {}

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}