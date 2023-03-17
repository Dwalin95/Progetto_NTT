package com.example.api;

import com.example.ntt.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface RequestApi {
    @GetMapping(value = "/{id}/receivedFriendRequests")
    ResponseEntity<Set<User>> findUserFriendRequestsById(@PathVariable String id);


    @GetMapping(value = "/{id}/sentFriendRequests")
    ResponseEntity<Set<User>> findUserSentFriendRequestById(@PathVariable String id);

    @PostMapping(value = "/{id}/sendFriendRequest")
    void sendFriendRequest(@PathVariable String id, @RequestParam String friendId);

    @PutMapping(value = "/{id}/manageFriendRequest/{friendId}")
    void handleFriendRequest(@PathVariable String id, @PathVariable String friendId, @RequestParam boolean accepted);

}