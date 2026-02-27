package com.example.muse.dto;

public record FriendResponse(
        Long friendUserId,
        String email,
        String friendName
) {}