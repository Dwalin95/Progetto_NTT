package com.example.ntt.controller;

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
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class UserController {

    private final MongoService mongoService;
    private final UserService userService;
    private final UserConfiguration userConfiguration;


    @RequestMapping("/")
    public String home() {
        return "La Home";
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findUserById(@PathVariable String id) {
        return userService.findUserByIdService(id);
    }

    @GetMapping(value = "/{id}/friends")
    public ResponseEntity<List<User>> findUserFriendsById(@PathVariable String id) {
        return userService.findFriendsByIdService(id);
    }

    @GetMapping(value = "/{id}/messages/{friendId}")
    public ResponseEntity<List<Message>> findUserMessagesByFriendId(@PathVariable String id, @PathVariable String friendId) {
        return userService.findMessagesByFriendIdsService(id, friendId);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> findAllUsers() {
        return userService.findAllUserService();
    }

    @GetMapping(value = "/userCount")
    public ResponseEntity<List<UserCountPerCity>> userCountPerCity() {
        return userService.userCountPerCityService();
    }

    @GetMapping(value = "/{id}/friendsPerCity")
    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCity(@PathVariable String id) {
        return userService.friendsCountPerCityService(id);
    }

    @GetMapping(value = "/signin")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String pwz) {
        return userConfiguration.checkLogin(email, pwz);
    }

    @GetMapping(value = "/{id}/receivedFriendRequests")
    public ResponseEntity<List<User>> findUserFriendRequestsById(@PathVariable String id) {
        return userService.findUserFriendRequestsByIdService(id);
    }

    @GetMapping(value = "/{id}/sentFriendRequests")
    public ResponseEntity<List<User>> findUserSentFriendRequestById(@PathVariable String id) {
        return userService.findUserSentFriendRequestByIdService(id);
    }

    @PostMapping(value = "/{id}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String id, @RequestParam String friendId) {
        userService.sendFriendRequestService(id, friendId);
    }

    @PostMapping(value = "/{id}/sendMessage/{friendId}")
    public void sendMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String body) {
        userService.sendMessageService(id, friendId, body);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<User> updateUserById(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    ) {
        return userService.updateUserByIdService(id, username, firstName, lastName, email, gender);
    }

    @PutMapping(value = "/updatePassword/{id}")
    public ResponseEntity<User> updatePasswordById(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        //il frontend deve fare il check sul momento se il field "new password" e li field "confirm password sono uguali"
        return userService.updatePasswordByIdService(id, oldPassword, confirmPassword);
    }

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user) {
        userConfiguration.validateSignUp(user);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }
}