package com.example.ntt.api;

import com.example.ntt.dto.user.*;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.user.IUsernamePic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public interface UserApi {

    @GetMapping(value = "/friends")
    ResponseEntity<Set<IUsernamePic>> findUserFriendsById(@RequestBody UserIdDTO userId);

    @PutMapping(value = "/password") // ex {id}/updatePassword
    ResponseEntity<User> updatePasswordById(@RequestBody UserUpdatePasswordDTO newUserPassword); //oldPassword, newPassword, confirmPassword

    @GetMapping(value = "/friendsPerCity")
    ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(@RequestBody UserIdDTO userId);

    @PutMapping(produces="application/json", value = "/user") //Put = "update" -> /user
    ResponseEntity<User> updateUserById(@RequestBody UserInfoWithIdDTO userInfo);

    @PutMapping(value = "/friend")
    void removeFriend(@RequestBody CurrentUserIdAndFriendIdDTO userIds);

    //TODO: LDB - non funge WHYYYY
    @GetMapping(value = "/users")
    ResponseEntity<List<User>> findAllUsers();

    @PostMapping(value = "/signin") //TODO: FC - cambiare l'endpoint in user/signin?
    ResponseEntity<User> login(@RequestBody UserAuthDTO credentials);

    @PostMapping(value = "/signup") //TODO: FC - cambiare l'endpoint in user/signup?
    void createUser(@RequestBody User user);

    @DeleteMapping(value = "/user/delete")
    void deleteUserById(@RequestBody UserIdDTO userId);

    /**
     *vedere cosa fargli restituire
     */
    @GetMapping(value = "/user")
    ResponseEntity<User> findUserByUsername(@RequestBody UsernameOnlyDTO username);

}