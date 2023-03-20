package com.example.ntt.controller;

import com.example.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.dto.UsernameOnlyDTO;
import com.example.ntt.dto.NamesOnlyDTO;
import com.example.ntt.model.Message;
import com.example.ntt.model.Post;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import com.example.ntt.service.ApplicationService;
import com.example.ntt.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.util.*;

@AllArgsConstructor
@RestController
public class UserController implements UserApi {

    private final ApplicationService applicationService;
    private final MongoService mongoService;

    private final UserConfiguration userConfiguration;

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
    public ResponseEntity<User> updateUserById(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender) {
        return ResponseEntity.ok(applicationService.updateUserById(id, username, firstName, lastName, email, gender));
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

    @GetMapping(value = "/{id}/friendsPerCity")
    public ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.friendsCountPerCity(id));
    }

    @GetMapping(value = "/signin")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String pwz) {
        return ResponseEntity.ok(userConfiguration.checkLogin(email, pwz));
    }

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

    @GetMapping(value = "/{id}/receivedFriendRequests")
    public ResponseEntity<Set<User>> findUserFriendRequestsById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.findUserReceivedFriendRequestsById(id));
    }

    @GetMapping(value = "/{id}/sentFriendRequests")
    public ResponseEntity<Set<User>> findUserSentFriendRequestById(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.findUserSentFriendRequestById(id));
    }

    @GetMapping(value = "/{id}/friendsPost")
    public ResponseEntity<List<Post>> findAllFriendsPost(@PathVariable String id){
        return ResponseEntity.ok(applicationService.findAllFriendsPosts(id));
    }

    @PostMapping(value = "/{id}/sendFriendRequest")
    public void sendFriendRequest(@PathVariable String id, @RequestParam String friendId) {
        applicationService.sendFriendRequest(id, friendId);
    }

    @PostMapping(value = "/{id}/sendMessage/{friendId}")
    public void sendMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String body) {
        applicationService.sendMessage(id, friendId, body);
    }

    @PutMapping(value = "/{id}/deleteMessage/{friendId}")
    public void deleteMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String messageId){
        applicationService.deleteMessage(id, friendId, messageId);
    }

    @PutMapping(value = "/{id}/deleteChat")
    public void deleteChat(@PathVariable String id, @RequestParam String friendId){
        applicationService.deleteChat(id, friendId);
    }

    //TODO: cambiare con il body/DTO - usa requestbody
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<User> updateUserById(
            @PathVariable String id,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> firstName,
            @RequestParam Optional<String> lastName,
            @RequestParam Optional<String> email,
            @RequestParam Optional<String> gender
    ) {
        return ResponseEntity.ok(applicationService.updateUserById(id, username, firstName, lastName, email, gender));
    }

    @PutMapping(value = "/{id}/manageFriendRequest/{friendId}")
    public void handleFriendRequest(@PathVariable String id, @PathVariable String friendId, @RequestParam boolean accepted){
        applicationService.handleFriendRequest(id, friendId, accepted);
    }

    @PutMapping(value = "{id}/removeFriend/{friendId}")
    public void removeFriend(@PathVariable String id, @PathVariable String friendId){
        applicationService.removeFriend(id, friendId);
    }

    @PutMapping(value = "{id}/removePost")
    public void removePost(@PathVariable String id, String postId){
        applicationService.removePost(id, postId);
    }

    @PutMapping(value = "{id}/updatePassword")
    public ResponseEntity<User> updatePasswordById(
            @PathVariable String id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        //il frontend deve fare il check sul momento se il field "new password" e li field "confirm password sono uguali"
        return ResponseEntity.ok(applicationService.updatePasswordById(id, oldPassword, confirmPassword));
    }

    @PostMapping(value = "/{id}/createPost")
    public void createPost(@PathVariable String id, @RequestBody Post post){
        applicationService.createPost(id, post);
    }

    @PostMapping(value = "/signup")
    public void createUser(@RequestBody User user) {
    @Override
    public void createUser(User user) {
        userConfiguration.validateSignUp(user);
    }

    @Override
    public void deleteUserById(String id) {
        mongoService.deleteUserById(id);
    }
}
