package com.example.ntt.controller;

import com.example.ntt.api.UserApi;
import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.user.*;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.user.IUsernamePic;
import com.example.ntt.service.ApplicationService;
import com.example.ntt.service.MongoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@AllArgsConstructor
@RestController
public class UserController implements UserApi {

    private final ApplicationService applicationService;
    private final MongoService mongoService;

    private final UserConfiguration userConfiguration;

    @Override
    public ResponseEntity<Set<IUsernamePic>> findUserFriendsById(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findFriendsById(userId));
    }

    @Override
    public ResponseEntity<User> findUserByUsername(UsernameOnlyDTO username){
        return ResponseEntity.ok(applicationService.findUserByUsername(username));
    }

    @Override
    public ResponseEntity<User> updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return ResponseEntity.ok(applicationService.updatePasswordById(newUserPassword));
    }

    @Override
    public ResponseEntity<Set<UserCountPerCity>> friendsCountPerCity(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.friendsCountPerCity(userId));
    }

    @Override
    public void updateUserById(UserInfoWithIdDTO userInfo) {
        applicationService.updateUserById(userInfo);
    }

    @Override
    public void removeFriend(CurrentUserFriendIdDTO userIds) {
        applicationService.removeFriend(userIds);
    }

    @Override
    public ResponseEntity<List<User>> findAllUsers() {
        return ResponseEntity.ok(mongoService.findAllUsers());
    }

    @Override
    public ResponseEntity<User> login(UserAuthDTO credentials) {
        return ResponseEntity.ok(userConfiguration.checkLogin(credentials));
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
