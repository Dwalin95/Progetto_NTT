package com.example.ntt.dto.request;

import lombok.Getter;

@Getter
public class FriendRequestDTO {

    private String currentUserId;
    private String friendId;
    private boolean isRequestAccepted;
}
