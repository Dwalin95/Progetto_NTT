package com.example.ntt.dto;

import lombok.Data;
import lombok.Value;

@Value
public class MessageSentIdsDTO {
    private String currentUserId;
    private String friendId;
    private String messageId;
}
