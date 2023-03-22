package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.UpdatedUser;
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
    public EmailGenderOnlyDTO getUserEmailGender(String username) {
        return mongoService.getUserEmailGender(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(USER_NOT_FOUND_ERROR_MSG, username))));
    }
    //Projection
    public UserFriendsAndRequestReceivedList getFriendsAndRequestReceived(String username) {
        return mongoService.getFriendsAndRequestReceived(username)
                .orElseThrow(() -> new ResourceNotFoundException((String.format(USER_NOT_FOUND_ERROR_MSG, username))));
    }


    public User updatePasswordById(String id, String oldPassword, String confirmedPassword) {
        return mongoService.findUserById(id)
                        .map(u -> this.doesNotMatch(oldPassword, u))
                        .map(u -> this.match(confirmedPassword, u))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
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

    public User findUserById(String id) {
        return mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    public Set<User> findFriendsById(String id) {
        return mongoService.findUserById(id)
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)))
                        .orElse(new HashSet<>());
    }

    public Set<UserCountPerCity> friendsCountPerCity(String id) {
        return mongoService.findUserById(id)
                        .map(User::getFriends)
                        .map(mongoService::countFriendsPerCityAggregation)
                        .orElseThrow(() -> new ResourceNotFoundException("No friends found"));
    }

    //TODO: capire perchè email e lastname non gli piacciono
    public User updateUserById(String id, UpdatedUser updatedUser) {
        return mongoService.findUserById(id)
                .map(u -> {
                    mongoService.saveUser(u.withUsername(updatedUser.getUsername().orElse(u.getUsername()))
                            .withFirstName(updatedUser.getFirstName().orElse(u.getFirstName()))
                            .withLastName(String.valueOf(updatedUser.getLastName().orElse(u.getLastName())))
                            .withEmail(String.valueOf(updatedUser.getEmail().orElse(u.getEmail())))
                            .withGender(updatedUser.getGender().orElse(u.getGender())));
                    return u;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    public void removeFriend(String currentUserId, String friendUserId){
        this.handleRemoveFriend(currentUserId, friendUserId);
        this.handleRemoveFriend(friendUserId, currentUserId);
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

