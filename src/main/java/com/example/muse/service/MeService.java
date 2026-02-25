package com.example.muse.service;

import com.example.muse.domain.User;
import com.example.muse.dto.MeResponse;
import com.example.muse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;

    // ✅ 내 정보 조회
    @Transactional(readOnly = true)
    public MeResponse getMe(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        return new MeResponse(
                u.getId(),
                u.getEmail(),
                u.getLoginId(),
                u.getNickname(),
                u.getRole()
        );
    }

    // ✅ 닉네임 수정
    @Transactional
    public String updateNickname(String email, String nickname) {

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String newNickname = nickname.trim();

        if (newNickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어 있을 수 없습니다.");
        }

        if (newNickname.length() > 20) {
            throw new IllegalArgumentException("닉네임은 20자 이하로 입력해주세요.");
        }

        u.setNickname(newNickname); // JPA dirty checking → 자동 update

        return u.getNickname();
    }
}