package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.user.*;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.PreconditionFailedException;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.projections.user.UserContactInfoProjection;
import com.example.ntt.projections.user.UserFriendsAndRequestReceivedListProjection;
import com.example.ntt.projections.user.UserFriendsListWithUsernameAndProfilePicProjection;
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
    public UserFriendsAndRequestReceivedListProjection getFriendsAndRequestReceived(String username) {
        return mongoService.getFriendsAndRequestReceived(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username))));
    }

    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword) {
        return mongoService.findUserById(newUserPassword.getId())
                .filter(user -> this.compareInsertedPasswordWithDbPassword(newUserPassword.getOldPassword(), user))
                .filter(user -> this.checkIfEqual(newUserPassword.getOldPassword(), user))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), newUserPassword.getId())));
    }
//                .map(user -> this.compareInsertedPasswordWithDbPassword(newUserPassword.getOldPassword(), user))
//                .map(user -> this.checkIfEqual(newUserPassword.getOldPassword(), user))
//                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), newUserPassword.getId())));
//
// }


    /**

      */
    private boolean checkIfEqual(String confirmedPassword, User user) {
        if (!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPassword())) {
            user.setPassword(userConfiguration.passwordEncoder().encode(confirmedPassword));
            mongoService.saveUser(user);
            return true;
        } else {
            throw new UnauthorizedException(ErrorMsg.NEW_PWS_EQUAL_TO_OLD_PSW.getMsg());
        }
    }

    /**
     * questo metodo confronta la password inserita dall'utente con quella presente nel db
     */
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
        return mongoService.findUserByUsername(username.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), username.getUsername())));
    }

    public Set<UserFriendsListWithUsernameAndProfilePicProjection> findFriendsById(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .map(u -> mongoService.findUserFriendsReturningUsernameAndProfilePicById(u.getFriends()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())))
                .orElse(new HashSet<>());
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId) {
        return mongoService.findUserById(userId.getId())
                .map(User::getFriends)
                .map(mongoService::countFriendsPerCityAggregation)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.NO_FRIENDS_FOUND.getMsg()));
    }

    //TODO: Da migliorare, rimessi gli optional
    public User updateUserById(UserInfoWithIdDTO userInfo) { //TODO: Test update 21.03.2023 - FC
        return mongoService.findUserById(userInfo.getId())
                .map(u -> this.handleException(userInfo, u))
                .map(u -> this.saveUpdatedUser(userInfo, u))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userInfo.getId())));
    }

    private User saveUpdatedUser(UserInfoWithIdDTO userInfo, User u) {
        mongoService.saveUser(u.withFirstName(userInfo.getFirstName().orElse(u.getFirstName()))
                .withUsername(userInfo.getUsername().orElse(u.getUsername()))
                .withLastName(userInfo.getLastName().orElse(u.getLastName()))
                .withEmail(userInfo.getEmail().orElse(u.getEmail()))
                .withGender(userInfo.getGender().orElse(u.getGender()))
                .withProfilePicUrl(userInfo.getProfilePicUrl().orElse(u.getProfilePicUrl()))
                .withVisible(userInfo.isVisible().orElse(u.isVisible())));
        return u;
    }

    private User handleException(UserInfoWithIdDTO userInfo, User user) {
        userConfiguration.handleUpdateException(userInfo.getProfilePicUrl().isPresent() && !userConfiguration.isImage(userInfo.getProfilePicUrl().get()),
                new PreconditionFailedException(ErrorMsg.URL_IS_NOT_IMG.getMsg()));
        userConfiguration.handleUpdateException(userInfo.getUsername().isPresent() && userConfiguration.usernameDoesNotExists(userInfo.getUsername().get()),
                new PreconditionFailedException(String.format(ErrorMsg.USERNAME_ALREADY_IN_USE.getMsg(), userInfo.getUsername())));
        userConfiguration.handleUpdateException(userInfo.getEmail().isPresent() && userConfiguration.emailDoesNotExists(userInfo.getEmail().get()),
                new PreconditionFailedException(String.format(ErrorMsg.EMAIL_ALREADY_IN_USE.getMsg(), userInfo.getEmail())));
        return user;
    }

    public void removeFriend(CurrentUserIdAndFriendIdDTO userIds) {
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

