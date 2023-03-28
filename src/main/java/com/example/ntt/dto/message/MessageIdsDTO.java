package com.example.ntt.dto.message;

import lombok.Data;
import lombok.Value;

@Value
public class MessageIdsDTO {
    private String currentUserId;
    private String messageId;
}
