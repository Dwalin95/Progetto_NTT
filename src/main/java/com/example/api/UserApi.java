package com.example.api;

import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface UserApi {

    @RequestMapping("/")
    String home();

    @PostMapping
    void createUser(@RequestBody User user);

    @DeleteMapping(value = "/delete/{id}")
    void deleteUserById(@PathVariable String id);

    @GetMapping(value = "/{id}")
    User findUserById(@PathVariable String id);

    @GetMapping(value = "/{id}/friends")
    Set<User> findUserFriendsById(@PathVariable String id);

    @GetMapping(value = "/{id}/messages/{friendId}")
    List<Message> findUserMessagesByFriendId(@PathVariable String id, @PathVariable String friendId);

    @GetMapping(value = "/list")
    List<User> findAllUsers();

    @GetMapping(value = "/userCount")
    Set<UserCountPerCity> userCountPerCity();

    @GetMapping(value = "/{id}/receivedFriendRequests")
    Set<User> findUserFriendRequestsById(@PathVariable String id);

    @GetMapping(value = "/{id}/sentFriendRequests")
    ResponseEntity<Set<User>> findUserSentFriendRequestById(@PathVariable String id);

    @PutMapping(value = "/update/{id}")
    public User updateUserById(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    );

    @PutMapping(value = "/updatePassword/{id}")
    public ResponseEntity<User> updatePasswordById(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    );

}
