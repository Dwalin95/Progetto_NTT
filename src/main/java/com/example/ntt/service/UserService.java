package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.*;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    //Projection
    public UserContactInfoProjection getUserContactInfo(String username) {
        return mongoService.getUserContactInfoByUsernameProjection(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, username)));
    }
    //DTO
    public EmailGenderOnlyDTO getUserEmailAndGender(String username) {
        return mongoService.getUserEmailAndGender(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(USER_NOT_FOUND_ERROR_MSG, username))));
    }
    //Projection
    public UserFriendsAndRequestReceivedList getFriendsAndRequestReceived(String username) {
        return mongoService.getFriendsAndRequestReceived(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(USER_NOT_FOUND_ERROR_MSG, username))));
    }


    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return mongoService.findUserById(newUserPassword.getId())
                        .map(user -> this.doesNotMatch(newUserPassword.getOldPassword(), user))
                        .map(user -> this.match(newUserPassword.getOldPassword(), user))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, newUserPassword.getId())));
    }

    private User match(String confirmedPassword, User user) {
        if(!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPassword())){
            user.setPassword(userConfiguration.passwordEncoder().encode(confirmedPassword));
            mongoService.saveUser(user);
            return user;
        } else {
            throw new UnauthorizedException("New password can't be equal to the old password");
        }
    }

    private User doesNotMatch(String oldPassword, User user) {
        if(userConfiguration.passwordEncoder().matches(oldPassword, user.getPassword())){
            return user;
        } else {
            throw new ResourceNotFoundException("Entered characters do not match the old password");
        }
    }

    public User findUserById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userId.getId())));
    }

    public Set<User> findFriendsById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userId.getId())))
                        .orElse(new HashSet<>());
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                        .map(User::getFriends)
                        .map(mongoService::countFriendsPerCityAggregation)
                        .orElseThrow(() -> new ResourceNotFoundException("No friends found"));
    }

    public User updateUserById(UserInfoWithIdDTO userInfo) { //TODO: Test update 21.03.2023
        return mongoService.findUserById(userInfo.getId())
                .map(u -> {
                    mongoService.saveUser(
                            u.withUsername(userInfo.getUsername())
                            .withFirstName(userInfo.getFirstName())
                            .withLastName(userInfo.getLastName())
                            .withEmail(userInfo.getEmail())
                            .withGender(userInfo.getGender()));
                    return u;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userInfo.getId())));
    }

    public void removeFriend(CurrentUserIdAndFriendIdDTO userIds){
        this.handleRemoveFriend(userIds.getCurrentUserId(), userIds.getFriendId());
        this.handleRemoveFriend(userIds.getFriendId(), userIds.getCurrentUserId());
    }

    private void handleRemoveFriend(String currentUserId, String friendUserId) {
        mongoService.findUserById(currentUserId)
                    .map(currentUser -> this.removeFriendFromList(friendUserId, currentUser))
                    .map(mongoService::saveUser)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    private User removeFriendFromList(String friendUserId, User user) {
        user.getFriends().remove(friendUserId);
        return user;
    }


}

