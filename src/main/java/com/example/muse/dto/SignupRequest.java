package com.example.muse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {

    @NotBlank
    @Size(min = 4, max = 30)
    private String loginId;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @NotBlank
    private String passwordConfirm;

    @NotBlank
    @Email
    @Size(max = 320)
    private String email;

    @Size(max = 30)
    private String nickname; // optional
}