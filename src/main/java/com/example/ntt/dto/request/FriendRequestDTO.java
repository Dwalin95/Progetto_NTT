package com.example.ntt.dto.request;

import lombok.Data;
import lombok.Value;

@Value
public class FriendRequestDTO {
    private String currentUserId;
    private String friendId;
    private boolean isRequestAccepted;
}
