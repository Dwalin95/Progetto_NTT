package com.example.ntt.service;

import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.model.*;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final UserService userService;
    private final MessageService messageService;
    private final RequestService requestService;
    private final PostService postService;

    //Projections
    public UserContactInfoProjection getUserContactInfo(String username) {
        return userService.getUserContactInfo(username);
    }
    //DTO
    public EmailGenderOnlyDTO getEmailGenderOnly(String username) {
        return userService.getUserEmailGender(username);
    }

    //Projection
    public UserFriendsAndRequestReceivedList getFriendsAndRequestReceived(String username) {
        return userService.getFriendsAndRequestReceived(username);
    }

    public User updatePasswordById(String id, String oldPassword, String confirmedPassword){
        return userService.updatePasswordById(id, oldPassword, confirmedPassword);
    }

    public User findUserById(String id){
        return userService.findUserById(id);
    }

    public Set<User> findFriendsById(String id){
        return userService.findFriendsById(id);
    }

    public Set<UserCountPerCity> friendsCountPerCity(String id){
        return userService.friendsCountPerCity(id);
    }

    public User updateUserById(String id, UpdatedUser updatedUser){
        return userService.updateUserById(id, updatedUser);
    }


    public void removeFriend(String currentUserId, String friendUserId){
        userService.removeFriend(currentUserId, friendUserId);
    }


    public Set<String> findAllMessageSenders(String id){
        return messageService.findAllMessageSenders(id);
    }

    public List<Message> findMessagesByFriendIds(String id, String friendId){
        return messageService.findMessagesByFriendIds(id, friendId);
    }

    public List<Message> findMessageByTextGlobal(String currentUserId, String text){
        return messageService.findMessageByTextGlobal(currentUserId, text);
    }

    public List<Message> findMessageByTextPerFriend(String currentUserId, String friendId, String text){
        return messageService.findMessageByTextPerFriend(currentUserId, friendId, text);
    }

    public void deleteSentMessage(String id, String friendId, String messageId){
        messageService.deleteSentMessage(id, friendId, messageId);
    }

    public void deleteReceivedMessage(String id, String messageId){
        messageService.deleteReceivedMessage(id, messageId);
    }

    public void deleteChat(String id, String friendId){
        messageService.deleteChat(id, friendId);
    }

    public void sendMessage(String id, String friendId, String body){
        messageService.sendMessage(id, friendId, body);
    }

    public Set<User> findUserReceivedFriendRequestsById(String id){
        return requestService.findUserReceivedFriendRequestsById(id);
    }

    public Set<User> findUserSentFriendRequestById(String currentUserId){
        return requestService.findUserSentFriendRequestById(currentUserId);
    }

    public void sendFriendRequest(String id, String friendId){
        requestService.sendFriendRequest(id, friendId);
    }

    public void handleFriendRequest(String id, String friendId, boolean accepted){
        requestService.handleFriendRequest(id, friendId, accepted);
    }

    public void createPost(String id, Post post){
        postService.createPost(id, post);
    }

    public void updatePost(String currentUserId, String postId, UpdatedPost updatedPost){
        postService.updatePost(currentUserId, postId, updatedPost);
    }

    public void deletePost(String id, String postId){
        postService.deletePost(id, postId);
    }

    public List<Post> findAllFriendsPosts(String id){
        return postService.findAllFriendsPosts(id);
    }


}
