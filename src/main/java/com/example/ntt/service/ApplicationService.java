package com.example.ntt.service;

import com.example.ntt.dto.*;
import com.example.ntt.model.*;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
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
    public EmailGenderOnlyDTO getUserEmailAndGender(String username) {
        return userService.getUserEmailAndGender(username);
    }

    //Projection
    public UserFriendsAndRequestReceivedList getFriendsAndRequestReceived(String username) {
        return userService.getFriendsAndRequestReceived(username);
    }

    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword){
        return userService.updatePasswordById(newUserPassword);
    }

    public User findUserById(UserIdDTO userId){
        return userService.findUserById(userId);
    }

    public User findUserByUsername(UsernameOnlyDTO username){
        return userService.findUserByUsername(username);
    }

    public Set<User> findFriendsById(UserIdDTO userId){
        return userService.findFriendsById(userId);
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId){
        return userService.friendsCountPerCity(userId);
    }

    public User updateUserById(UserInfoWithIdDTO userInfo){
        return userService.updateUserById(userInfo);
    }


    public void removeFriend(CurrentUserIdAndFriendIdDTO userIds){
        userService.removeFriend(userIds);
    }


    public Set<String> findAllMessageSenders(UserIdDTO userId){
        return messageService.findAllMessageSenders(userId);
    }

    public List<Message> findMessagesByFriendIds(CurrentUserIdAndFriendIdDTO userIds){
        return messageService.findMessagesByFriendIds(userIds);
    }

    public List<Message> findMessageByTextGlobal(String currentUserId, String text){
        return messageService.findMessageByTextGlobal(currentUserId, text);
    }

    public List<Message> findMessageByTextPerFriend(String currentUserId, String friendId, String text){
        return messageService.findMessageByTextPerFriend(currentUserId, friendId, text);
    }

    public void deleteSentMessage(MessageSentIdsDTO messageSent){
        messageService.deleteSentMessage(messageSent);
    }

    public void deleteReceivedMessage(MessageIdsDTO deleteMessage){
        messageService.deleteReceivedMessage(deleteMessage);
    }

    public void deleteChat(CurrentUserIdAndFriendIdDTO userIds){
        messageService.deleteChat(userIds);
    }

    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend){
        messageService.sendMessage(messageToSend);
    }

    public Set<User> findUserReceivedFriendRequestsById(UserIdDTO userId){
        return requestService.findUserReceivedFriendRequestsById(userId);
    }

    public Set<User> findUserSentFriendRequestById(UserIdDTO currentUserId){
        return requestService.findUserSentFriendRequestById(currentUserId);
    }

    public void sendFriendRequest(CurrentUserIdAndFriendIdDTO userIds){
        requestService.sendFriendRequest(userIds);
    }

    public void handleFriendRequest(FriendRequestDTO friendRequest){
        requestService.handleFriendRequest(friendRequest);
    }

    public void createPost(PostDTO post){
        postService.createPost(post);
    }

    public void updatePost(PostDTO postDTO){
        postService.updatePost(postDTO);
    }

    public void deletePost(PostIdAndUserIdDTO postDTO){
        postService.deletePost(postDTO);
    }

    public List<Post> findAllFriendsPosts(UserIdDTO userId){
        return postService.findAllFriendsPosts(userId);
    }

    public void createComment(CommentDTO commentDTO){
        postService.createComment(commentDTO);
    }
}
