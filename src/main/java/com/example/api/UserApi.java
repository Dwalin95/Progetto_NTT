package com.example.api;

import com.example.ntt.model.UpdatedUser;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public interface UserApi {

    @GetMapping(value = "/{id}/friends")
    ResponseEntity<Set<User>> findUserFriendsById(@PathVariable String id);

    @GetMapping(value = "/{id}")
    ResponseEntity<User> findUserById(@PathVariable String id);

    @PutMapping(value = "{id}/updatePassword")
    ResponseEntity<User> updatePasswordById(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    );

    @GetMapping(value = "/{id}/friendsPerCity")
    ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(@PathVariable String id);

    @PutMapping(value = "/update/{id}")
    ResponseEntity<User> updateUserById(@PathVariable String id, @RequestBody UpdatedUser updatedUser);

    @PutMapping(value = "{id}/removeFriend/{friendId}")
    void removeFriend(@PathVariable String id, @PathVariable String friendId);

    @GetMapping(value = "/list")
    ResponseEntity<List<User>> findAllUsers();

    @GetMapping(value = "/signin")
    ResponseEntity<User> login(@RequestParam String email, @RequestParam String password);

    @PostMapping(value = "/signup")
    void createUser(@RequestBody User user);

    @DeleteMapping(value = "/delete/{id}")
    public void deleteUserById(@PathVariable String id);
}