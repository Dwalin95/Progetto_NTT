package com.example.ntt.controller;
import com.example.ntt.api.RequestApi;
import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.request.FriendRequestDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.projections.user.UserReceivedFriendRequestsProjection;
import com.example.ntt.projections.user.UserSentFriendRequestsProjection;
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
    public ResponseEntity<Set<UserReceivedFriendRequestsProjection>> findUserReceivedFriendRequestsById(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(userId));
    }

    @Override
    public ResponseEntity<Set<UserSentFriendRequestsProjection>> findUserSentFriendRequestById(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(userId));
    }

    @Override
    public void sendFriendRequest(CurrentUserIdAndFriendIdDTO userIds) {
        applicationService.sendFriendRequest(userIds);
    }

    @Override
    public void handleFriendRequest(FriendRequestDTO friendRequest) {
        applicationService.handleFriendRequest(friendRequest);
    }
}
