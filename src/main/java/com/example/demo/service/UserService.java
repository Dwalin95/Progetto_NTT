package com.example.demo.service;

import com.example.demo.exceptionHandler.ResourceNotFoundException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.model.UserCountPerCity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;

    public ResponseEntity<User> updateUserById(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender){

        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        user.setUsername(username.orElse(user.getUsername()));
        user.setFirstName(firstName.orElse(user.getFirstName()));
        user.setLastName(lastName.orElse(user.getLastName()));
        user.setEmail(email.orElse(user.getEmail()));
        user.setGender(gender.orElse(user.getGender()));

        mongoService.saveUser(user);

        return ResponseEntity.ok(user);
    }

    public void sendMessage(String username, String friendUsername, String body){
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        User messageReceiver = mongoService.findUserByUsername(friendUsername).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendUsername)));

        Message message = Message.builder()
                .body(body)
                .senderUsername(username)
                .receiverUsername(friendUsername)
                .timestamp(new Date())
                .build();

        user.getMessages().add(message);
        messageReceiver.getMessages().add(message);
        mongoService.saveUser(user);
        mongoService.saveUser(messageReceiver);
    }

    public void sendFriendRequest(String username, String friendUsername){
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        User friendToAdd = mongoService.findUserByUsername(friendUsername).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendUsername)));

        user.getSentFriendRequests().add(friendUsername);
        friendToAdd.getReceivedFriendRequests().add(username);

        mongoService.saveUser(user);
        mongoService.saveUser(friendToAdd);
    }

    public ResponseEntity<List<User>> findUserFriendRequestsByUsername(String username){
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        List<String> friendRequestsList = user.getReceivedFriendRequests();
        List<User> users = mongoService.findUserFriendsByUsername(friendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<List<User>> findUserSentFriendRequestByUsername(String username){
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        List<String> sentFriendRequestsList = user.getSentFriendRequests();
        List<User> users = mongoService.findUserFriendsByUsername(sentFriendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCity(String username){
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User with username: %s not found", username)));
        List<String> count = user.getFriends();
        return ResponseEntity.ok(mongoService.countFriendsPerCityAggregation(count));
    }
}
