package com.example.ntt.controller;

import com.example.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.model.Message;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
import com.example.ntt.service.MongoService;
import com.example.ntt.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@AllArgsConstructor
@RestController
//annotazioni lasciate per passaggio non del tutto definitivo tra Controller ed interfaccie
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class UserController implements UserApi {

    private final MongoService mongoService;
    private final UserService userService;
    private final UserConfiguration userConfiguration;


    @Override
    public String home() {
        return "La Home";
    }

    @Override
    public User findUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }

    @Override
    public Set<User> findUserFriendsById(@PathVariable String id) {
        return userService.findFriendsById(id);
    }

    @GetMapping(value = "/{id}/chats")
    public Set<String> findAllMessageSenders(@PathVariable String id){
        return userService.findAllMessageSendersService(id);
    }

    @Override
    public List<Message> findUserMessagesByFriendId(@PathVariable String id, @PathVariable String friendId) {
        return userService.findMessagesByFriendIdsService(id, friendId);
    }

    @Override
    public List<User> findAllUsers() {
        return mongoService.findAllUsers();
    }

    @Override
    public Set<UserCountPerCity> userCountPerCity() {
        return mongoService.countUsersPerCityAggregation();
    }

    @GetMapping(value = "/{id}/friendsPerCity")
    public Set<UserCountPerCity> friendsCountPerCity(@PathVariable String id) {
        return userService.friendsCountPerCity(id);
    }

    @GetMapping(value = "/signin")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        return userConfiguration.checkLogin(email, password);
    }

    @Override
    public Set<User> findUserFriendRequestsById(@PathVariable String id) {
        return userService.findUserReceivedFriendRequestsById(id);
    }

    @Override
    public ResponseEntity<Set<User>> findUserSentFriendRequestById(@PathVariable String id) {
        return userService.findUserSentFriendRequestByIdService(id);
    }

    @PostMapping(value = "/{id}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String id, @RequestParam String friendId) {
        userService.sendFriendRequest(id, friendId);
    }

    @PostMapping(value = "/{id}/sendMessage/{friendId}")
    public void sendMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String body) {
        userService.sendMessage(id, friendId, body);
    }

    @PutMapping(value = "/{id}/deleteMessage/{friendId}")
    public void deleteMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String messageId){
        userService.deleteMessage(id, friendId, messageId);
    }

    @PutMapping(value = "/{id}/deleteChat")
    public void deleteChat(@PathVariable String id, @RequestParam String friendId){
        userService.deleteChat(id, friendId);
    }

    //TODO: cambiare con il body/DTO
    @Override
    public User updateUserById(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    ) {
        return userService.updateUserById(id, username, firstName, lastName, email, gender);
    }

    @PutMapping(value = "/{id}/manageFriendRequest/{friendId}")
    public void handleFriendRequest(@PathVariable String id, @PathVariable String friendId, @RequestParam boolean accepted){
        userService.handleFriendRequest(id, friendId, accepted);
    }

    @PutMapping(value = "{id}/removeFriend/{friendId}")
    public void removeFriend(@PathVariable String id, @PathVariable String friendId){
        userService.removeFriendService(id, friendId);
    }

    @Override
    public ResponseEntity<User> updatePasswordById(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        //il frontend deve fare il check sul momento se il field "new password" e li field "confirm password sono uguali"
        return userService.updatePasswordByIdService(id, oldPassword, confirmPassword);
    }

    @Override
    public void createUser(@RequestBody User user) {
        userConfiguration.validateSignUp(user);
    }

    @Override
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteById(id);
    }
}

