package com.example.demo.controller;

import com.example.demo.configuration.UserConfiguration;
import com.example.demo.exceptionHandler.ResourceNotFoundException;
import com.example.demo.model.UserCountPerCity;
import com.example.demo.model.User;
import com.example.demo.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class UserController {

    private final MongoService mongoService;
    @Autowired
    private UserConfiguration userConfiguration;


    @RequestMapping("/")
    public String home() {
        return "La Home";
    }

    @GetMapping(value = "/ciao")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        Optional<User> user = mongoService.findUserByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{username}/friends")
    public ResponseEntity<List<User>> findUserFriendsByUsername(@PathVariable String username) {
        User user = mongoService.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with username: %s not found", username)));
        List<User> friends = mongoService.findUserFriendsByUsername(user.getFriends()).orElse(new ArrayList<>());
        return ResponseEntity.ok(friends);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> findAllUsers() {
        Optional<List<User>> userList = Optional.of(mongoService.findAllUsers());
        return userList.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException("no users found"));
    }

    @GetMapping(value = "/userCount")
    public ResponseEntity<List<UserCountPerCity>> userCountPerCity() {
        Optional<List<UserCountPerCity>> userCount = Optional.of(mongoService.countUsersPerCityAggregation());
        return userCount.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException("no users found"));
    }

    @GetMapping(value = "/{username}/friendsPerCity")
    public ResponseEntity<List<UserCountPerCity>> friendsCountPerCity(@PathVariable String username) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User with username: %s not found", username)));
        List<String> count = user.getFriends();
        return ResponseEntity.ok(mongoService.countFriendsPerCityAggregation(count));
    }

    @GetMapping(value = "/user", params = {"email", "pwz"})
    public ResponseEntity<User> login(@RequestParam(value = "email") String email, @RequestParam("pwz") String pwz) throws Exception {
        return userConfiguration.checkLogin(email, pwz);
    }

    @GetMapping(value = "/{username}/receivedFriendRequests")
    public ResponseEntity<List<User>> findUserFriendRequestsByUsername(@PathVariable String username) {
        Optional<User> user = mongoService.findUserByUsername(username);
        if (user.isPresent()) {
            List<String> friendRequestsList = user.get().getReceivedFriendRequests();
            List<User> friends = mongoService.findUserFriendsByUsername(friendRequestsList).orElse(new ArrayList<>());
            return ResponseEntity.ok(friends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{username}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String username, @RequestParam String friendUsername) {
        User user = mongoService.findUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", username)));
        User friendToAdd = mongoService.findUserByUsername(friendUsername).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendUsername)));

        user.getSentFriendRequests().add(friendUsername);
        friendToAdd.getReceivedFriendRequests().add(username);

        mongoService.saveUser(user);
        mongoService.saveUser(friendToAdd);
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

        Optional<User> user = mongoService.findUserById(id);

        if (user.isPresent()) {
            user.get().setUsername(username.orElse(user.get().getUsername()));
            user.get().setFirstName(firstName.orElse(user.get().getFirstName()));
            user.get().setLastName(lastName.orElse(user.get().getLastName()));
            user.get().setEmail(email.orElse(user.get().getEmail()));
            user.get().setGender(gender.orElse(user.get().getGender()));

            mongoService.saveUser(user.get());

            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user) throws Exception {
        userConfiguration.validateSignUp(user);
    }

    @DeleteMapping(value = "delete/{id}")
    public void deleteUserById(@PathVariable String id) {
        mongoService.deleteUserById(id);
    }
}

