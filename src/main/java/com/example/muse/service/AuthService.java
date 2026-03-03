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
     * ✅ 아이디(loginId) 사용 가능 여부
     * - true: 사용 가능 (중복 아님)
     * - false: 사용 불가 (중복/형식오류)
     */
    @Transactional(readOnly = true)
    public boolean isLoginIdAvailable(String loginId) {
        if (loginId == null) return false;
        String id = loginId.trim();
        if (id.isEmpty()) return false;

        return !userRepository.existsByLoginId(id);
    }

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest req) {

        if (req == null) throw new IllegalArgumentException("요청 값이 비어있습니다.");

        String email = normalizeEmail(req.getEmail());
        String loginId = normalizeLoginId(req.getLoginId());
        String password = req.getPassword();
        String passwordConfirm = req.getPasswordConfirm();
        String nickname = normalizeNickname(req.getNickname());

        // 1) 필수값 체크
        if (email == null) throw new IllegalArgumentException("이메일을 입력해주세요.");
        if (loginId == null) throw new IllegalArgumentException("아이디를 입력해주세요.");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        if (passwordConfirm == null || passwordConfirm.isBlank()) throw new IllegalArgumentException("비밀번호 확인을 입력해주세요.");

        // 2) 비밀번호 확인
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }

        // 3) 이메일 중복
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 4) 아이디 중복
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 5) 저장
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
    public String login(String login, String password) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("이메일 또는 아이디를 입력해주세요.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        String value = login.trim();
        User u;

        // 아주 단순하고 실용적인 판별: '@' 있으면 이메일로 본다
        if (value.contains("@")) {
            String email = value.toLowerCase();
            u = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("이메일/아이디 또는 비밀번호가 올바르지 않습니다."));
        } else {
            String loginId = value;
            u = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("이메일/아이디 또는 비밀번호가 올바르지 않습니다."));
        }

        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new IllegalArgumentException("이메일/아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return jwtUtil.createToken(u.getEmail(), u.getRole());
    }

    // ====== helpers ======

    private String normalizeEmail(String email) {
        if (email == null) return null;
        String e = email.trim().toLowerCase();
        return e.isEmpty() ? null : e;
    }

    private String normalizeLoginId(String loginId) {
        if (loginId == null) return null;
        String id = loginId.trim();
        return id.isEmpty() ? null : id;
    }

    private String normalizeNickname(String nickname) {
        if (nickname == null) return null;
        String n = nickname.trim();
        return n.isEmpty() ? null : n;
    }
}