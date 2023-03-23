package com.example.ntt.api;

import com.example.ntt.dto.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.FriendRequestDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface RequestApi {
    @GetMapping(value = "/receivedFriendRequests")
    ResponseEntity<Set<User>> findUserFriendRequestsById(@RequestBody UserIdDTO userId);


    @GetMapping(value = "/sentFriendRequests")
    ResponseEntity<Set<User>> findUserSentFriendRequestById(@RequestBody UserIdDTO userId);

    @PostMapping(value = "/sendFriendRequest")
    void sendFriendRequest(@RequestParam CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param friendRequest {"currentUserId":<value>, "friendId":<value>, "isRequestAccepted":<value>}
     */
    @PutMapping(value = "/manageFriendRequest")
    void handleFriendRequest(@RequestParam FriendRequestDTO friendRequest);

}