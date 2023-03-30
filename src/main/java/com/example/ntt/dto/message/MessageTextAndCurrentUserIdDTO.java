package com.example.ntt.dto.message;

import lombok.Getter;

@Getter
public class MessageTextAndCurrentUserIdDTO {

    private String currentUserId;
    private String text;
}