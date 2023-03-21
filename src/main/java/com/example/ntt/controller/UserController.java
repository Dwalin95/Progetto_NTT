package com.example.ntt.controller;

import com.example.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.UsernameOnlyDTO;
import com.example.ntt.model.UpdatedUser;
import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import com.example.ntt.service.ApplicationService;
import com.example.ntt.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@AllArgsConstructor
@RestController
public class UserController implements UserApi {

    private final ApplicationService applicationService;
    private final MongoService mongoService;

    private final UserConfiguration userConfiguration;


    //-- [INIZIO] Interfaccia di proiezione e utilizzo dei DTO --//
    @PostMapping(value = "/userInfo")
    public ResponseEntity<UserContactInfoProjection> getContactInformation(@RequestBody UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getUserContactInfo(username.getUsername()));
    }

    @PostMapping(value = "/userEmailAndGender")
    public ResponseEntity<EmailGenderOnlyDTO> getEmailGenderOnly(@RequestBody UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getEmailGenderOnly(username.getUsername()));
    }
    //TODO: approfondire le consocenze in merito ai DTO e alle Projection, vedere se il codice è ottimizzato
    @PostMapping(value = "/friendListAndRequestReceived")
    public ResponseEntity<UserFriendsAndRequestReceivedList> getFriendListAndRequestReceived(@RequestBody UsernameOnlyDTO username) {
        return ResponseEntity.ok(applicationService.getFriendsAndRequestReceived(username.getUsername()));
    }

    /**
     * Post
     * Passi come Body
     * DTO all'andata [Username]
     * 1. Interfaccia di ritorno [Nome, Cognome, Email, Gender]
     * 2. DTO di ritorno in un secondo metodo [Email e Gender]
     * 3. Interfaccia di ritorno [friends e receivedFriendRequests]
     */

//-- [FINE] Interfaccia di proiezione e utilizzo dei DTO --//
//-- [Inizio] Aggiungi commento --//

//    @PostMapping(value="/{id}")

//-- [FINE] Aggiungi commento --//

    @Override
    public ResponseEntity<Set<User>> findUserFriendsById(String id) {
        return ResponseEntity.ok(applicationService.findFriendsById(id));
    }

    @Override
    public ResponseEntity<User> findUserById(String id) {
        return ResponseEntity.ok(applicationService.findUserById(id));
    }

    @Override
    public ResponseEntity<User> updatePasswordById(String id, String oldPassword, String newPassword, String confirmPassword) {
        return ResponseEntity.ok(applicationService.updatePasswordById(id, oldPassword, confirmPassword));

    }

    @Override
    public ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(String id) {
        return ResponseEntity.ok(applicationService.friendsCountPerCity(id));
    }

    @Override
    public ResponseEntity<User> updateUserById(String id, UpdatedUser updatedUser) {
        return ResponseEntity.ok(applicationService.updateUserById(id, updatedUser));
    }

    @Override
    public void removeFriend(String id, String friendId) {
        applicationService.removeFriend(id, friendId);
    }

    @Override
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(mongoService.findAllUsers());
    }

    @Override
    public ResponseEntity<User> login(String email, String password) {

        URI uri = URI.create("http://localhost:3000");
        return ResponseEntity.created(uri)
                .header("Access-Control-Allow-Origin","http://localhost:3000")
                .body(userConfiguration.checkLogin(email, password));
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
    public void deleteUserById(String id) {
        mongoService.deleteUserById(id);
    }
}