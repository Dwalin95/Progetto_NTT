package com.example.ntt.api;

import com.example.ntt.dto.*;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.dto.UsernameOnlyDTO;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public interface UserApi {

    //-- [INIZIO] Interfaccia di proiezione e utilizzo dei DTO --//
    @GetMapping(value = "/contactInfo")
    ResponseEntity<UserContactInfoProjection> getContactInformation(@RequestBody UsernameOnlyDTO username);

    @PostMapping(value = "/userEmailAndGender")
    ResponseEntity<EmailGenderOnlyDTO> getUserEmailAndGender(@RequestBody UsernameOnlyDTO username);

    //TODO: approfondire le consocenze in merito ai DTO e alle Projection, vedere se il codice è ottimizzato
    @GetMapping(value = "/friendsListAndRequest")
    ResponseEntity<UserFriendsAndRequestReceivedList> getFriendListAndRequestReceived(@RequestBody UsernameOnlyDTO username);

    @GetMapping(value = "/friends")
    ResponseEntity<Set<User>> findUserFriendsById(@RequestBody UserIdDTO userId);

    @GetMapping(value = "/user")
    ResponseEntity<User> findUserByUsername(@RequestBody UsernameOnlyDTO username);

    /*@GetMapping(value = "/user")
    ResponseEntity<User> findUserById(@RequestBody UserIdDTO userId);*/

    @PutMapping(value = "/password") // ex {id}/updatePassword
    ResponseEntity<User> updatePasswordById(@RequestBody UserUpdatePasswordDTO newUserPassword); //oldPassword, newPassword, confirmPassword

    @GetMapping(value = "/friendsPerCity")
    ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(@RequestBody UserIdDTO userId);

    @PutMapping(produces="application/json",value = "/user") //Put = "update" -> /user
    ResponseEntity<User> updateUserById(@RequestBody UserInfoWithIdDTO userInfo); //

    @PutMapping(value = "/friend")
    void removeFriend(@RequestBody CurrentUserIdAndFriendIdDTO userIds);

    @GetMapping(value = "/users")
    ResponseEntity<List<User>> findAllUsers();

    @PostMapping(value = "/signin") //TODO: cambiare l'endpoint in user/signin?
    ResponseEntity<User> login(@RequestBody UserAuthDTO credentials);

    @PostMapping(value = "/signup") //TODO: cambiare l'endpoint in user/signup?
    void createUser(@RequestBody User user);

    @DeleteMapping(value = "/user/delete")
    public void deleteUserById(@RequestBody UserIdDTO userId);
}