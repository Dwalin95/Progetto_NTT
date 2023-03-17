package com.example.ntt.service;

import com.example.ntt.model.Message;
import com.example.ntt.model.Post;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
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

    public User updateUserById(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender){
        return userService.updateUserById(id, username, firstName, lastName, email, gender);
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

    public void deleteMessage(String id, String friendId, String messageId){
        messageService.deleteMessage(id, friendId, messageId);
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

    public void removePost(String id, String postId){
        postService.removePost(id, postId);
    }

    public List<Post> findAllFriendsPosts(String id){
        return postService.findAllFriendsPosts(id);
    }
}
