package com.example.ntt.dto.message;

import lombok.Data;
import lombok.Value;

@Value
public class MessageToSendIdsAndBodyDTO {
    private String currentUserId;
    private String friendId;
    private String body;
}
