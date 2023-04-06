package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.user.*;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.user.IUsernamePic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;

    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return mongoService.findUserById(newUserPassword.getId())
                .filter(user -> this.compareInsertedPasswordWithDbPassword(newUserPassword.getOldPassword(), user))
                .filter(user -> this.checkIfEqual(newUserPassword.getNewPassword(), user))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), newUserPassword.getId())));
    }

    private boolean checkIfEqual(String newPassword, User user) {
        if (!userConfiguration.passwordEncoder().matches(newPassword, user.getPassword())) {
            user.setPassword(userConfiguration.passwordEncoder().encode(newPassword));
            mongoService.saveUser(user);
            return true;
        } else {
            throw new UnauthorizedException(ErrorMsg.NEW_PWS_EQUAL_TO_OLD_PSW.getMsg());
        }
    }

    private boolean compareInsertedPasswordWithDbPassword(String oldPassword, User user) {
        if (userConfiguration.passwordEncoder().matches(oldPassword, user.getPassword())) {
            return true;
        } else {
            throw new ResourceNotFoundException(ErrorMsg.NO_MATCH_OLD_PSW.getMsg());
        }
    }

    public User findUserById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())));
    }

    public User findUserByUsername(UsernameOnlyDTO username) {
        User user = mongoService.findUserByUsername(username.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username.getUsername())));
        if(user.isVisible() == true){
            //TODO: fare projection con le informazione del profilo
            return user;
        } else {
            //TODO: fare projection con solo username e profilePic
            return user;
        }
    }

    public Set<IUsernamePic> findFriendsById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .map(u -> mongoService.findUserFriendsUsernamePic(u.getFriends()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.NO_FRIENDS_FOUND.getMsg()));
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .map(User::getFriends)
                .map(mongoService::countFriendsPerCityAggr)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.NO_FRIENDS_FOUND.getMsg()));
    }

    public void updateUserById(UserInfoWithIdDTO userInfo) {
        mongoService.findUserById(userInfo.getId())
                .map(u -> this.saveUpdatedUser(userInfo, u))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userInfo.getId())));
    }

    private User saveUpdatedUser(UserInfoWithIdDTO userInfo, User user) {
        Optional.of(user)
                .map(u -> userInfo.getProfilePicUrl().isPresent() &&
                            userConfiguration.isImage(userInfo.getProfilePicUrl().get()))
                .map(u -> userInfo.getUsername().isPresent() &&
                            userConfiguration.usernameDoesNotExists(userInfo.getUsername().get()))
                .map(u -> userInfo.getEmail().isPresent() &&
                            userConfiguration.emailDoesNotExists(userInfo.getEmail().get()) &&
                            userConfiguration.validateEmail(userInfo.getEmail().get()));

        mongoService.saveUser(user.withFirstName(userInfo.getFirstName().orElse(user.getFirstName()))
                .withUsername(userInfo.getUsername().orElse(user.getUsername()))
                .withLastName(userInfo.getLastName().orElse(user.getLastName()))
                .withEmail(userInfo.getEmail().orElse(user.getEmail()))
                .withGender(userInfo.getGender().orElse(user.getGender()))
                .withProfilePicUrl(userInfo.getProfilePicUrl().orElse(user.getProfilePicUrl()))
                .withVisible(userInfo.isVisible().orElse(user.isVisible())));
        return user;
    }

    public void removeFriend(CurrentUserFriendIdDTO userIds) {
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

