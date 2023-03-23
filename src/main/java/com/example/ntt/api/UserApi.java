package com.example.ntt.api;

import com.example.ntt.dto.*;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public interface UserApi {



    @GetMapping(value = "/friends")
    ResponseEntity<Set<User>> findUserFriendsById(@RequestBody UserIdDTO userId);

    @GetMapping(value = "/user")
    ResponseEntity<User> findUserById(@RequestBody UserIdDTO userId);

    @PutMapping(value = "/password") // ex {id}/updatePassword
    ResponseEntity<User> updatePasswordById(@RequestBody UserUpdatePasswordDTO newUserPassword); //oldPassword, newPassword, confirmPassword

    @GetMapping(value = "/friendsPerCity")
    ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(@RequestBody UserIdDTO userId);

    @PutMapping(value = "/user") //Put = "update" -> /user
    ResponseEntity<User> updateUserById(@RequestBody UserInfoWithIdDTO userInfo); //

    @PutMapping(value = "/friend") //TODO: Put o Delete?
    void removeFriend(@RequestBody CurrentUserIdAndFriendIdDTO userIds);

    @GetMapping(value = "/users")
    ResponseEntity<List<User>> findAllUsers();

    @GetMapping(value = "/signin") //TODO: cambiare l'endpoint in user/signin?
    ResponseEntity<User> login(@RequestBody UserAuthDTO credentials);

    @PostMapping(value = "/signup") //TODO: cambiare l'endpoint in user/signup?
    void createUser(@RequestBody User user);

    @DeleteMapping(value = "/user/delete")
    public void deleteUserById(@RequestBody UserIdDTO userId);
}