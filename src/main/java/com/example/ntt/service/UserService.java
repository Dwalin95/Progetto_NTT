package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.*;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.PreconditionFailedException;
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

    //Projection
    public UserContactInfoProjection getUserContactInfo(String username) {
        return mongoService.getUserContactInfoByUsernameProjection(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username)));
    }

    //DTO
    public EmailGenderOnlyDTO getUserEmailAndGender(String username) {
        return mongoService.getUserEmailAndGender(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username))));
    }

    //Projection
    public UserFriendsAndRequestReceivedList getFriendsAndRequestReceived(String username) {
        return mongoService.getFriendsAndRequestReceived(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username))));
    }

    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return mongoService.findUserById(newUserPassword.getId())
                        .map(user -> this.doesNotMatch(newUserPassword.getOldPassword(), user))
                        .map(user -> this.match(newUserPassword.getOldPassword(), user))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), newUserPassword.getId())));
    }

    private User match(String confirmedPassword, User user) {
        if(!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPassword())){
            user.setPassword(userConfiguration.passwordEncoder().encode(confirmedPassword));
            mongoService.saveUser(user);
            return user;
        } else {
            throw new UnauthorizedException(ErrorMsg.NEW_PWS_EQUAL_TO_OLD_PSW.getMsg());
        }
    }

    private User doesNotMatch(String oldPassword, User user) {
        if(userConfiguration.passwordEncoder().matches(oldPassword, user.getPassword())){
            return user;
        } else {
            throw new ResourceNotFoundException(ErrorMsg.NO_MATCH_OLD_PSW.getMsg());
        }
    }

    public User findUserById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())));
    }

    public Set<User> findFriendsById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())))
                        .orElse(new HashSet<>());
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                        .map(User::getFriends)
                        .map(mongoService::countFriendsPerCityAggregation)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.NO_FRIENDS_FOUND.getMsg()));
    }
    //TODO: DTO - FC
    //TODO: trovare il modo di togliere tutti gli if - LDB
    public User updateUserById(UserInfoWithIdDTO userInfo) { //TODO: Test update 21.03.2023 - FC
        return mongoService.findUserById(userInfo.getId())
                .map(u -> {
                    mongoService.saveUser(u.withFirstName(userInfo.getFirstName()))
                            .withLastName(String.valueOf(userInfo.getLastName()))
                            .withGender(userInfo.getGender())
                            .withProfilePicUrl(userInfo.getProfilePicUrl());

                    if(userConfiguration.usernameExists(userInfo.getUsername())){
                        throw new PreconditionFailedException(String.format("The username %s is already present in use", userInfo.getUsername()));
                    } else {
                        mongoService.saveUser(u.withUsername(userInfo.getUsername()));
                    }
                    if(userConfiguration.emailExists(userInfo.getEmail())){
                        throw new PreconditionFailedException(String.format("The email %s is already present in use", userInfo.getEmail()));
                    } else {
                        mongoService.saveUser(u.withEmail(String.valueOf(userInfo.getEmail())));
                    }
                    return u;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userInfo.getId())));
    }

    public void removeFriend(CurrentUserIdAndFriendIdDTO userIds){
        this.handleRemoveFriend(userIds.getCurrentUserId(), userIds.getFriendId());
        this.handleRemoveFriend(userIds.getFriendId(), userIds.getCurrentUserId());
    }

    private void handleRemoveFriend(String currentUserId, String friendUserId) {
        mongoService.findUserById(currentUserId)
                    .map(currentUser -> this.removeFriendFromList(friendUserId, currentUser))
                    .map(mongoService::saveUser)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), currentUserId)));
    }

    private User removeFriendFromList(String friendUserId, User user) {
        user.getFriends().remove(friendUserId);
        return user;
    }
}

