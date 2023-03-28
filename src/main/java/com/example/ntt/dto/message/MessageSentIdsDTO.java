package com.example.ntt.dto.message;

import lombok.Data;
import lombok.Value;

@Value
public class MessageSentIdsDTO {
    private String currentUserId;
    private String friendId;
    private String messageId;
}
