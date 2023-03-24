package com.example.ntt.controller;

import com.example.ntt.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.*;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import com.example.ntt.service.ApplicationService;
import com.example.ntt.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@RestController
public class UserController implements UserApi {

    private final ApplicationService applicationService;
    private final MongoService mongoService;

    private final UserConfiguration userConfiguration;


    //-- [INIZIO] Interfaccia di proiezione e utilizzo dei DTO --//
    @Override
    public ResponseEntity<UserContactInfoProjection> getContactInformation(UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getUserContactInfo(username.getUsername()));
    }

    @Override
    public ResponseEntity<EmailGenderOnlyDTO> getUserEmailAndGender(UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getUserEmailAndGender(username.getUsername()));
    }

    //TODO: approfondire le consocenze in merito ai DTO e alle Projection, vedere se il codice è ottimizzato
    @Override
    public ResponseEntity<UserFriendsAndRequestReceivedList> getFriendListAndRequestReceived(UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getFriendsAndRequestReceived(username.getUsername()));
    }

    //-- [FINE] Interfaccia di proiezione e utilizzo dei DTO --//
    //-- [Inizio] Aggiungi commento --//

    //    @PostMapping(value="/{id}")

    //-- [FINE] Aggiungi commento --//

    @Override
    public ResponseEntity<Set<User>> findUserFriendsById(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findFriendsById(userId));
    }

    @Override
    public ResponseEntity<User> findUserByUsername(UsernameOnlyDTO username){
        return ResponseEntity.ok(applicationService.findUserByUsername(username));
    }

    /*@Override
    public ResponseEntity<User> findUserById(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findUserById(userId));
    }*/

    @Override
    public ResponseEntity<User> updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return ResponseEntity.ok(applicationService.updatePasswordById(newUserPassword));

    }

    @Override
    public ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.friendsCountPerCity(userId));
    }

    //DTO Attributes: id, username, firstName, lastName, email, gender
    @Override
    public ResponseEntity<User> updateUserById(UserInfoWithIdDTO userInfo) {
        return ResponseEntity.ok(applicationService.updateUserById(userInfo));
    }

    @Override
    public void removeFriend(CurrentUserIdAndFriendIdDTO userIds) {
        applicationService.removeFriend(userIds);
    }

    @Override
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(mongoService.findAllUsers());
    }

    @Override
    public ResponseEntity<User> login(UserAuthDTO credentials) {

        URI uri = URI.create("http://localhost:3000");
        return ResponseEntity.created(uri)
                .header("Access-Control-Allow-Origin","http://localhost:3000")
                .body(userConfiguration.checkLogin(credentials));
       /*
        return ResponseEntity.ok(userConfiguration.checkLogin(email, password))
                .getHeaders()
                .add("Access-Control-Allow-Origin","http://localhost:3000");
    */
    }

    @Override
    public void createUser(User user) {
        userConfiguration.validateSignUp(user);
    }

    @Override
    public void deleteUserById(UserIdDTO userId) {
        mongoService.deleteUserById(userId);
    }

}
