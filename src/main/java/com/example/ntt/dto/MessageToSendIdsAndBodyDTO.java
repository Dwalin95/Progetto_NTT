package com.example.ntt.dto;

import lombok.Data;

@Data
public class MessageToSendIdsAndBodyDTO {
    private String currentUserId;
    private String friendId;
    private String body;
}
