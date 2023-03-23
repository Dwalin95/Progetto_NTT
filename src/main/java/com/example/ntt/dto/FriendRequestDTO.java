package com.example.ntt.dto;

import lombok.Data;

@Data
public class FriendRequestDTO {
    private String currentUserId;
    private String friendId;
    private boolean isRequestAccepted;
}
