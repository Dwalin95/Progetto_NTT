package com.example.demo.service;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.exceptionHandler.ResourceNotFoundException;
import com.example.demo.exceptionHandler.UnauthorizedException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.model.UserCountPerCity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;

    public ResponseEntity<User> updatePasswordById(String id, String oldPassword, String confirmedPassword){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        if(userConfiguration.passwordEncoder().matches(oldPassword, user.getPwz())){
            if(!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPwz())){
                user.setPwz(userConfiguration.passwordEncoder().encode(confirmedPassword));
                mongoService.saveUser(user);
                return ResponseEntity.ok(user);
            } else {
                throw new UnauthorizedException("New password can't be equal to the old password");
            }
        } else {
            throw new ResourceNotFoundException("Entered characters do not match the old password");
        }
    }

    public ResponseEntity<User> findUserByUsernameService(String username) {
        Optional<User> user = mongoService.findUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with username: %s not found", username)));
    }
    public ResponseEntity<List<User>> findFriendsByUsernameService(String username) {
        User user = mongoService.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with username: %s not found", username)));
        List<User> friends = mongoService.findUserFriendsByUsername(user.getFriends()).orElse(new ArrayList<>());
        return ResponseEntity.ok(friends);
    }

    public ResponseEntity<List<Message>> findMessagesByFriendUsernameService(String username, String frinedUsername) {
        User user = mongoService.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with username: %s not found", username)));
        List<Message> messages = user.getMessages();
        return ResponseEntity.ok(messages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList()));
    }

    public ResponseEntity<List<User>> findAllUserService() {
        Optional<List<User>> userList = Optional.of(mongoService.findAllUsers());
        return userList.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException("No users found"));
    }
    public  ResponseEntity<List<UserCountPerCity>> userCountPerCityService(){
        Optional<List<UserCountPerCity>> userCount = Optional.of(mongoService.countUsersPerCityAggregation());
        return userCount.map(ResponseEntity::ok).orElseThrow(()->new ResourceNotFoundException("No users found"));

    }

    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCityService(String username) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User with username: %s not found", username)));
        List<String> count = user.getFriends();
        return ResponseEntity.ok(mongoService.countFriendsPerCityAggregation(count));
    }
    public ResponseEntity<List<User>> findUserFriendRequestsByUsernameService(String username) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        List<String> friendRequestsList = user.getReceivedFriendRequests();
        List<User> users = mongoService.findUserFriendsByUsername(friendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<List<User>> findUserSentFriendRequestByUsernameService(String username) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        List<String> sentFriendRequestsList = user.getSentFriendRequests();
        List<User> users = mongoService.findUserFriendsByUsername(sentFriendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> updateUserByIdService(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender) {

        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        user.setUsername(username.orElse(user.getUsername()));
        user.setFirstName(firstName.orElse(user.getFirstName()));
        user.setLastName(lastName.orElse(user.getLastName()));
        user.setEmail(email.orElse(user.getEmail()));
        user.setGender(gender.orElse(user.getGender()));

        mongoService.saveUser(user);

        return ResponseEntity.ok(user);
    }

    public void sendFriendRequestService(String username, String friendUsername) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        User friendToAdd = mongoService.findUserByUsername(friendUsername).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendUsername)));

        user.getSentFriendRequests().add(friendUsername);
        friendToAdd.getReceivedFriendRequests().add(username);

    }

    public void sendMessageService(String username, String friendUsername, String body) {
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





}
