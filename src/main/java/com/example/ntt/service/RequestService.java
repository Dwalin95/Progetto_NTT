package com.example.ntt.service;

import com.example.ntt.dto.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.FriendRequestDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final MongoService mongoService;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public Set<User> findUserReceivedFriendRequestsById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .map(User::getReceivedFriendRequests)
                .map(mongoService::findUserFriendsById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userId.getId())))
                .orElse(new HashSet<>());
    }

    public Set<User> findUserSentFriendRequestById(UserIdDTO currentUserId) {
        return mongoService.findUserById(currentUserId.getId())
                .map(User::getSentFriendRequests)
                .map(mongoService::findUserFriendsById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)))
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
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    //TODO: da ritrasformare perchè l'altro non funzionava
    public void handleFriendRequest(FriendRequestDTO friendRequest){
        User user = mongoService.findUserById(friendRequest.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendRequest.getCurrentUserId())));
        User friend = mongoService.findUserById(friendRequest.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendRequest.getFriendId())));

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

    /*public void handleFriendRequest(String id, String friendId, boolean accepted){
        this.handleSingleFriendRequest(id, friendId, accepted);
        this.handleSingleFriendRequest(friendId, id, accepted);
    }

    private void handleSingleFriendRequest(String id, String friendId, boolean accepted) {
        mongoService.findUserById(id)
                .map(u -> this.removeRequest(friendId, u))
                .filter(u -> accepted)
                .ifPresent(u -> u.getFriends().add(friendId));
        mongoService.findUserById(id)
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User removeRequest(String friendId, User u) {
        u.getReceivedFriendRequests().remove(friendId);
        return u;
    }*/
}
