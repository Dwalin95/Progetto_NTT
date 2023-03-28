package com.example.ntt.controller;
import com.example.ntt.api.RequestApi;
import com.example.ntt.dto.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.FriendRequestDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.User;
import com.example.ntt.projections.UserReceivedFriendRequestsProjection;
import com.example.ntt.projections.UserSentFriendRequestsProjection;
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
    public ResponseEntity<Set<UserReceivedFriendRequestsProjection>> findUserReceivedFriendRequestsById(@RequestBody UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(userId));
    }

    @Override
    public ResponseEntity<Set<UserSentFriendRequestsProjection>> findUserSentFriendRequestById(@RequestBody UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(userId));
    }

    @Override
    public void sendFriendRequest(@RequestParam CurrentUserIdAndFriendIdDTO userIds) {
        applicationService.sendFriendRequest(userIds);
    }

    @Override
    public void handleFriendRequest(@RequestParam FriendRequestDTO friendRequest) {
        applicationService.handleFriendRequest(friendRequest);
    }
}
