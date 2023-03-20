package com.example.ntt.service;

import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final MongoService mongoService;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public Set<User> findUserReceivedFriendRequestsById(String id) {
        return mongoService.findUserById(id)
                .map(User::getReceivedFriendRequests)
                .map(mongoService::findUserFriendsById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)))
                .orElse(new HashSet<>());
    }

    public Set<User> findUserSentFriendRequestById(String currentUserId) {
        return mongoService.findUserById(currentUserId)
                .map(User::getSentFriendRequests)
                .map(mongoService::findUserFriendsById)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)))
                .orElse(new HashSet<>());
    }

    public void sendFriendRequest(String id, String friendId){
        this.handleRequest(friendId, user -> this.addReceivedFriendRequestToFriendUser(id, user));
        this.handleRequest(id, user -> this.addFriendRequestToCurrentUser(friendId, user));
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
    public void handleFriendRequest(String id, String friendId, boolean accepted){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        User friend = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));

        if(accepted){
            user.getReceivedFriendRequests().remove(friendId);
            friend.getSentFriendRequests().remove(id);
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
        } else {
            user.getSentFriendRequests().remove(friendId);
            friend.getReceivedFriendRequests().remove(id);
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
