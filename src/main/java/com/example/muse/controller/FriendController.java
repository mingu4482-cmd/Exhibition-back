package com.example.muse.controller;

import com.example.muse.dto.FriendAddRequest;
import com.example.muse.dto.FriendRenameRequest;
import com.example.muse.dto.FriendResponse;
import com.example.muse.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    // 친구 추가: POST /api/friends  body: { "email": "friend@test.com" }
    @PostMapping
    public Map<String, Object> add(Authentication auth, @RequestBody @Valid FriendAddRequest req) {
        String myEmail = (String) auth.getPrincipal();
        friendService.addFriend(myEmail, req.getEmail());
        return Map.of("ok", true);
    }

    // 친구 목록: GET /api/friends
    @GetMapping
    public List<FriendResponse> list(Authentication auth) {
        String myEmail = (String) auth.getPrincipal();
        return friendService.list(myEmail);
    }

    // 친구 이름 수정: PATCH /api/friends/{friendUserId} body: { "friendName": "친구1" }
    @PatchMapping("/{friendUserId}")
    public Map<String, Object> rename(
            Authentication auth,
            @PathVariable Long friendUserId,
            @RequestBody @Valid FriendRenameRequest req
    ) {
        String myEmail = (String) auth.getPrincipal();
        friendService.rename(myEmail, friendUserId, req.getFriendName());
        return Map.of("ok", true);
    }

    // 친구 삭제: DELETE /api/friends/{friendUserId}
    @DeleteMapping("/{friendUserId}")
    public Map<String, Object> remove(Authentication auth, @PathVariable Long friendUserId) {
        String myEmail = (String) auth.getPrincipal();
        friendService.remove(myEmail, friendUserId);
        return Map.of("ok", true);
    }
}