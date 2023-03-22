package com.example.ntt.controller;
import com.example.ntt.api.RequestApi;
import com.example.ntt.model.User;
import com.example.ntt.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
public class RequestController implements RequestApi {

    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<Set<User>> findUserReceivedFriendRequestsById(String currentUserId) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(currentUserId));
    }

    @Override
    public ResponseEntity<Set<User>> findUserSentFriendRequestById(String currentUserId) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(currentUserId));
    }

    @Override
    public void sendFriendRequest(String currentUserId, String friendId) {
        applicationService.sendFriendRequest(currentUserId, friendId);
    }

    @Override
    public void handleFriendRequest(String currentUserId, String friendId, boolean accepted) {
        applicationService.handleFriendRequest(currentUserId, friendId, accepted);
    }
}
