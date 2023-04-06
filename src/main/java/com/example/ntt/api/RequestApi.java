package com.example.ntt.api;

import com.example.ntt.dto.user.CurrentUserFriendIdDTO;
import com.example.ntt.dto.request.FriendRequestDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.projections.user.IUsernamePic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface RequestApi {

    @GetMapping(value = "/receivedFriendRequests")
    ResponseEntity<Set<IUsernamePic>> findUserReceivedFriendRequestsById(@RequestBody UserIdDTO userId);

    @GetMapping(value = "/sentFriendRequests")
    ResponseEntity<Set<IUsernamePic>> findUserSentFriendRequestById(@RequestBody UserIdDTO userId);

    @PostMapping(value = "/sendFriendRequest")
    void sendFriendRequest(@RequestBody CurrentUserFriendIdDTO userIds);


    //TODO: LDB - capire perchè da requestBody il booleano anche se specificato true ritorna false
    @PutMapping(value = "/handleFriendRequest")
    void handleFriendRequest(@RequestBody FriendRequestDTO friendRequest);
}