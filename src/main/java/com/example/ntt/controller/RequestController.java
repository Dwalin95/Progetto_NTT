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
    public ResponseEntity<Set<User>> findUserFriendRequestsById(String id) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(id));
    }
    @Override
    public ResponseEntity<Set<User>> findUserSentFriendRequestById(String id) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(id));
    }

    @Override
    public void sendFriendRequest(String id, String friendId) {
        applicationService.sendFriendRequest(id, friendId);
    }

    @Override
    public void handleFriendRequest(String id, String friendId, boolean accepted) {
        applicationService.handleFriendRequest(id, friendId, accepted);
    }
}
