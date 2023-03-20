package com.example.ntt.controller;
import com.example.api.RequestApi;
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
    public ResponseEntity<Set<User>> findUserFriendRequestsById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(id));
    }
    @Override
    public ResponseEntity<Set<User>> findUserSentFriendRequestById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(id));
    }

    @Override
    public void sendFriendRequest(@PathVariable String id, @RequestParam String friendId) {
        applicationService.sendFriendRequest(id, friendId);
    }

    @Override
    public void handleFriendRequest(@PathVariable String id, @PathVariable String friendId, @RequestParam boolean accepted) {
        applicationService.handleFriendRequest(id, friendId, accepted);
    }
}
