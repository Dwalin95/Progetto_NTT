package com.example.ntt.dto.message;

import lombok.Getter;

@Getter
public class MessageToSendIdsAndBodyDTO {

    private String currentUserId;
    private String friendId;
    private String body;
}