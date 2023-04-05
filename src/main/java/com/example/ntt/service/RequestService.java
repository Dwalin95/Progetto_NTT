package com.example.ntt.service;

import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.request.FriendRequestDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.User;
import com.example.ntt.projections.user.UserReceivedFriendRequestsProjection;
import com.example.ntt.projections.user.UserSentFriendRequestsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final MongoService mongoService;

    public Set<UserReceivedFriendRequestsProjection> findUserReceivedFriendRequestsById(UserIdDTO userId) {
        Set<UserReceivedFriendRequestsProjection> set = mongoService.findUserById(userId.getId())
                .map(User::getReceivedFriendRequests)
                .map(mongoService::findUserReceivedFriendRequestById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())))
                .orElse(new HashSet<>());
        return set;
    }

    public Set<UserSentFriendRequestsProjection> findUserSentFriendRequestById(UserIdDTO currentUserId) {
        return mongoService.findUserById(currentUserId.getId())
                .map(User::getSentFriendRequests)
                .map(mongoService::findUserSentFriendRequestById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), currentUserId)))
                .orElse(new HashSet<>());
    }

    public void sendFriendRequest(CurrentUserIdAndFriendIdDTO userIds){
        this.handleRequest(userIds.getFriendId(), user -> this.addReceivedFriendRequestToFriendUser(userIds.getCurrentUserId(), user));
        this.handleRequest(userIds.getCurrentUserId(), user -> this.addFriendRequestToCurrentUser(userIds.getFriendId(), user));
    }

    private User addFriendRequestToCurrentUser(String friendId, User user) {
        user.getSentFriendRequests().add(friendId);
        return user;
    }

    private User addReceivedFriendRequestToFriendUser(String id, User user) {
        user.getReceivedFriendRequests().add(id);
        return user;
    }

    private void handleRequest(String id, UnaryOperator<User> addRequest){
        mongoService.findUserById(id)
                .map(addRequest)
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), id)));
    }

    //TODO: LDB - da ritrasformare perchè l'altro non funzionava
    public void handleFriendRequest(FriendRequestDTO friendRequest){
        User user = mongoService.findUserById(friendRequest.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), friendRequest.getCurrentUserId())));
        User friend = mongoService.findUserById(friendRequest.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), friendRequest.getFriendId())));

        if(friendRequest.isRequestAccepted()){
            user.getReceivedFriendRequests().remove(friendRequest.getFriendId());
            friend.getSentFriendRequests().remove(friendRequest.getCurrentUserId());
            user.getFriends().add(friendRequest.getFriendId());
            friend.getFriends().add(friendRequest.getCurrentUserId());
        } else {
            user.getSentFriendRequests().remove(friendRequest.getFriendId());
            friend.getReceivedFriendRequests().remove(friendRequest.getCurrentUserId());
        }
        mongoService.saveUser(user);
        mongoService.saveUser(friend);
    }
}
