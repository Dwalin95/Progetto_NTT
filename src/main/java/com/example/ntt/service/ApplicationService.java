package com.example.ntt.service;

import com.example.ntt.dto.comment.CommentDTO;
import com.example.ntt.dto.comment.CommentIdAndPostIdDTO;
import com.example.ntt.dto.message.*;
import com.example.ntt.dto.post.PostDTO;
import com.example.ntt.dto.post.PostIdAndUserIdDTO;
import com.example.ntt.dto.request.FriendRequestDTO;
import com.example.ntt.dto.user.*;
import com.example.ntt.model.*;
import com.example.ntt.projections.IUsernamePicLastMsg;
import com.example.ntt.projections.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final UserService userService;
    private final MessageService messageService;
    private final RequestService requestService;
    private final PostService postService;

    public User updatePasswordById(UserUpdatePasswordDTO newUserPassword){
        return userService.updatePasswordById(newUserPassword);
    }

    public User findUserById(UserIdDTO userId){
        return userService.findUserById(userId);
    }

    public User findUserByUsername(UsernameOnlyDTO username){
        return userService.findUserByUsername(username);
    }

    public Set<IUsernamePic> findFriendsById(UserIdDTO userId){
        return userService.findFriendsById(userId);
    }

    public Set<UserCountPerCity> friendsCountPerCity(UserIdDTO userId){
        return userService.friendsCountPerCity(userId);
    }

    public void updateUserById(UserInfoWithIdDTO userInfo){
        userService.updateUserById(userInfo);
    }


    public void removeFriend(CurrentUserFriendIdDTO userIds){
        userService.removeFriend(userIds);
    }

    public Set<IUsernamePicLastMsg> findAllMessageSenders(UserIdDTO userId){
        return messageService.findAllMessageSenders(userId);
    }

    public List<Message> findMessagesByFriendIds(CurrentUserFriendIdDTO userIds){
        return messageService.findMessagesByFriendIds(userIds);
    }

    public List<Message> findMessageByTextGlobal(MessageTextAndCurrentUserIdDTO messageByText){
        return messageService.findMessageByTextGlobal(messageByText);
    }

    public List<Message> findMessageByTextPerFriend(MessageTextAndCurrentUserAndFriendIdDTO messageByText){
        return messageService.findMessageByTextPerFriend(messageByText);
    }

    public void deleteSentMessage(MessageSentIdsDTO messageSent){
        messageService.deleteSentMessage(messageSent);
    }

    public void deleteReceivedMessage(MessageReceivedIdsDTO deleteMessage){
        messageService.deleteReceivedMessage(deleteMessage);
    }

    public void deleteChat(CurrentUserFriendIdDTO userIds){
        messageService.deleteChat(userIds);
    }

    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend){
        messageService.sendMessage(messageToSend);
    }

    public Set<IUsernamePic> findUserReceivedFriendRequestsById(UserIdDTO userId){
        return requestService.findUserReceivedFriendRequestsById(userId);
    }

    public Set<IUsernamePic> findUserSentFriendRequestById(UserIdDTO currentUserId){
        return requestService.findUserSentFriendRequestById(currentUserId);
    }

    public void sendFriendRequest(CurrentUserFriendIdDTO userIds){
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

    public void deleteComment(CommentIdAndPostIdDTO commentIdAndPostId) {
        postService.deleteComment(commentIdAndPostId);
    }
}
