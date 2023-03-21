package com.example.ntt.controller;

import com.example.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.model.UpdatedUser;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.service.ApplicationService;
import com.example.ntt.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
