package com.example.ntt.dto;

import lombok.Data;

@Data
public class MessageSentIdsDTO {
    private String currentUserId;
    private String friendId;
    private String messageId;
}
