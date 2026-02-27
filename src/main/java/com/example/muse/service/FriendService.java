package com.example.muse.service;

import com.example.muse.domain.Friend;
import com.example.muse.domain.User;
import com.example.muse.dto.FriendResponse;
import com.example.muse.repository.FriendRepository;
import com.example.muse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    private User me(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    private User byEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일 유저가 없습니다."));
    }

    @Transactional
    public void addFriend(String myEmail, String friendEmail) {
        User me = me(myEmail);
        User friend = byEmail(friendEmail);

        if (me.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("자기 자신은 친구로 추가할 수 없습니다.");
        }

        if (friendRepository.existsByUserIdAndFriendUserId(me.getId(), friend.getId())) {
            return; // 이미 친구면 그냥 OK
        }

        Friend f = Friend.builder()
                .userId(me.getId())
                .friendUserId(friend.getId())
                .friendName(null)
                .build();

        friendRepository.save(f);
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> list(String myEmail) {
        User me = me(myEmail);

        return friendRepository.findAllByUserIdOrderByIdDesc(me.getId()).stream()
                .map(fr -> {
                    User u = userRepository.findById(fr.getFriendUserId())
                            .orElseThrow(() -> new IllegalArgumentException("친구 유저가 존재하지 않습니다."));
                    return new FriendResponse(u.getId(), u.getEmail(), fr.getFriendName());
                })
                .toList();
    }

    @Transactional
    public void rename(String myEmail, Long friendUserId, String friendName) {
        User me = me(myEmail);

        Friend f = friendRepository.findByUserIdAndFriendUserId(me.getId(), friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 없습니다."));

        f.setFriendName(friendName.trim());
    }

    @Transactional
    public void remove(String myEmail, Long friendUserId) {
        User me = me(myEmail);

        friendRepository.findByUserIdAndFriendUserId(me.getId(), friendUserId)
                .ifPresent(friendRepository::delete);
    }
}