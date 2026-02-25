package com.example.muse.service;

import com.example.muse.domain.User;
import com.example.muse.dto.SignupRequest;
import com.example.muse.repository.UserRepository;
import com.example.muse.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest req) {

        String email = req.getEmail().trim().toLowerCase();
        String loginId = req.getLoginId().trim();
        String password = req.getPassword();
        String passwordConfirm = req.getPasswordConfirm();
        String nickname = req.getNickname();

        // 1️⃣ 비밀번호 확인 체크
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 2️⃣ 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 3️⃣ 아이디 중복 체크
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 4️⃣ 사용자 생성 (builder 없이)
        User u = new User();
        u.setEmail(email);
        u.setLoginId(loginId);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole("USER");
        u.setNickname(nickname);

        userRepository.save(u);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public String login(String email, String password) {

        String e = email.trim().toLowerCase();

        User u = userRepository.findByEmail(e)
                .orElseThrow(() -> new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("이메일/비밀번호가 올바르지 않습니다.");
        }

        return jwtUtil.createToken(u.getEmail(), u.getRole());

    }
}
