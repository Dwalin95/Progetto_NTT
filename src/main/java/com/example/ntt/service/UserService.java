package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
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

    public ResponseEntity<User> updatePasswordByIdService(String id, String oldPassword, String confirmedPassword) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        if (userConfiguration.passwordEncoder().matches(oldPassword, user.getPwz())) {
            if (!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPwz())) {
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

    public ResponseEntity<User> findUserByIdService(String id) {
        Optional<User> user = mongoService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", username)));
    }

    public ResponseEntity<List<User>> findFriendsByIdService(String id) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        List<User> friends = mongoService.findUserFriendsById(user.getFriends()).orElse(new ArrayList<>());
        return ResponseEntity.ok(friends);
    }

    public ResponseEntity<List<Message>> findMessagesByFriendIdsService(String id, String friendId) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        User friend = mongoService.findUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        List<Message> friendMessages = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), friend.get_id());
        return ResponseEntity.ok(friendMessages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList()));
    }

    public ResponseEntity<List<User>> findAllUserService() {
        Optional<List<User>> userList = Optional.of(mongoService.findAllUsers());
        return userList.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException("No users found"));
    }

    public ResponseEntity<List<UserCountPerCity>> userCountPerCityService() {
        Optional<List<UserCountPerCity>> userCount = Optional.of(mongoService.countUsersPerCityAggregation());
        return userCount.map(ResponseEntity::ok).orElseThrow(()->new ResourceNotFoundException("No users found"));
    }

    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCityService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
        List<String> count = user.getFriends();
        return ResponseEntity.ok(mongoService.countFriendsPerCityAggregation(count));
    }
    public ResponseEntity<List<User>> findUserFriendRequestsByIdService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        List<String> friendRequestsList = user.getReceivedFriendRequests();
        List<User> users = mongoService.findUserFriendsById(friendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<List<User>> findUserSentFriendRequestByIdService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        List<String> sentFriendRequestsList = user.getSentFriendRequests();
        List<User> users = mongoService.findUserFriendsById(sentFriendRequestsList).orElse(new ArrayList<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> updateUserByIdService(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender) {

        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        User updatedUser = mongoService.saveUser(user.withUsername(username.orElse(user.getUsername()))
                .withFirstName(firstName.orElse(user.getFirstName()))
                .withLastName(lastName.orElse(user.getLastName()))
                .withEmail(email.orElse(user.getEmail()))
                .withGender(gender.orElse(user.getGender())));

        return ResponseEntity.ok(updatedUser);
    }

    public void sendFriendRequestService(String id, String friendId) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        User friendToAdd = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendId)));

        user.getSentFriendRequests().add(friendId);
        friendToAdd.getReceivedFriendRequests().add(id);

    }

    public void sendMessageService(String id, String friendId, String body) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendId)));

        Message message = Message.builder()
                .body(body)
                .senderId(id)
                .receiverId(friendId)
                .timestamp(new Date())
                .build();

        user.getMessages().add(message);
        messageReceiver.getMessages().add(message);
        mongoService.saveUser(user);
        mongoService.saveUser(messageReceiver);
    }
}
