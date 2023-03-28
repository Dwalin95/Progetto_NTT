package com.example.ntt.api;

import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.request.FriendRequestDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.projections.UserReceivedFriendRequestsProjection;
import com.example.ntt.projections.UserSentFriendRequestsProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface RequestApi {
    @GetMapping(value = "/receivedFriendRequests")
    ResponseEntity<Set<UserReceivedFriendRequestsProjection>> findUserReceivedFriendRequestsById(@RequestBody UserIdDTO userId);


    @GetMapping(value = "/sentFriendRequests")
    ResponseEntity<Set<UserSentFriendRequestsProjection>> findUserSentFriendRequestById(@RequestBody UserIdDTO userId);

    @PostMapping(value = "/sendFriendRequest")
    void sendFriendRequest(@RequestParam CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param friendRequest {"currentUserId":<value>, "friendId":<value>, "isRequestAccepted":<value>}
     */
    @PutMapping(value = "/manageFriendRequest")
    void handleFriendRequest(@RequestParam FriendRequestDTO friendRequest);

}