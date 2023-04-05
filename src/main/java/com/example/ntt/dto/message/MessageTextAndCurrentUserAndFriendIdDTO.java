package com.example.ntt.dto.message;

import lombok.Getter;

@Getter
public class MessageTextAndCurrentUserAndFriendIdDTO {

    private String currentUserId;
    private String friendId;
    private String text;
}