package com.example.demo.controller;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.model.Message;
import com.example.demo.model.UserCountPerCity;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import com.example.demo.service.UserService;
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

    @GetMapping(value = "/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        return userService.findUserByUsernameService(username);
    }

    @GetMapping(value = "/{username}/friends")
    public ResponseEntity<List<User>> findUserFriendsByUsername(@PathVariable String username) {
        return userService.findFriendsByUsernameService(username);
    }

    @GetMapping(value = "/{username}/messages/{friendUsername}")
    public ResponseEntity<List<Message>> findUserMessagesByFriendUsername(@PathVariable String username, @PathVariable String friendUsername){
        return userService.findMessagesByFriendUsernameService(username,friendUsername);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> findAllUsers() {
        return userService.findAllUserService();
    }

    @GetMapping(value = "/userCount")
    public ResponseEntity<List<UserCountPerCity>> userCountPerCity() {
        return userService.userCountPerCityService();
    }

    @GetMapping(value = "/{username}/friendsPerCity")
    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCity(@PathVariable String username) {
        return userService.friendsCountPerCityService(username);
    }

    @GetMapping(value = "/signin")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String pwz){
        return userConfiguration.checkLogin(email, pwz);
    }

    @GetMapping(value = "/{username}/receivedFriendRequests")
    public ResponseEntity<List<User>> findUserFriendRequestsByUsername(@PathVariable String username) {
        return userService.findUserFriendRequestsByUsernameService(username);
    }

    @GetMapping(value = "/{username}/sentFriendRequests")
    public ResponseEntity<List<User>> findUserSentFriendRequestByUsername(@PathVariable String username){
        return userService.findUserSentFriendRequestByUsernameService(username);
    }

    @PostMapping(value = "/{username}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String username, @RequestParam String friendUsername) {
        userService.sendFriendRequestService(username, friendUsername);
    }

    @PostMapping(value = "/{username}/sendMessage/{friendUsername}")
    public void sendMessage(@PathVariable String username, @PathVariable String friendUsername, @RequestParam String body){
        userService.sendMessageService(username, friendUsername, body);
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
    ){
        //il frontend deve fare il check sul momento se il field "new password" e li field "confirm password sono uguali"
        return userService.updatePasswordById(id, oldPassword, confirmPassword);
    }

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user){
        userConfiguration.validateSignUp(user);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }
}