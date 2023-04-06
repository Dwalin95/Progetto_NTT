package com.example.ntt.service;

import com.example.ntt.dto.user.EmailGenderOnlyDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.*;
import com.example.ntt.projections.IUsernamePicLastMsg;
import com.example.ntt.projections.post.IPostIdAuthor;
import com.example.ntt.projections.user.*;
import com.example.ntt.repository.MessageRepository;
import com.example.ntt.repository.PostRepository;
import com.example.ntt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MongoService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;

    public Optional<User> findUserById(String id){
        return userRepository.findById(id);
    }

    public Optional<EmailGenderOnlyDTO> getUserEmailAndGender(String username) {
        return this.userRepository.findByUsername(username, EmailGenderOnlyDTO.class);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<Set<IUsernamePic>> findUserFriendsUsernamePic(Set<String> friendsIds){
        return userRepository.findFriendsById(friendsIds, IUsernamePic.class);
    }

    public Optional<Set<IUsernamePicLastMsg>> findUserFriendsUsernamePicLastMsg(Set<String> friendsIds){
        return userRepository.findFriendsById(friendsIds, IUsernamePicLastMsg.class);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public void deleteUserById(UserIdDTO userId){
        userRepository.deleteById(userId.getId());
    }

    public Set<IPostIdAuthor> findAllFriendsPostsIdsAggr(Set<String> friendsIds){
        return userRepository.findAllFriendsPostsIds(friendsIds, IPostIdAuthor.class);
    }

    public Set<UserCountPerCity> countFriendsPerCityAggr(Set<String> friendsUsernames){
        return userRepository.countFriendsPerCity(friendsUsernames);
    }

    public List<Message> getMessageListWithoutSpecifiedMessage(String username, String messageId){
        return messageRepository.getMessageListWithoutSpecifiedMessage(username, messageId);
    }

    public Message findSingleMessage(String username, String messageId){
        return messageRepository.findSingleMessage(username, messageId);
    }

    public List<Message> findMessagesWithoutSpecifiedInteraction(String currentUserId, String senderId, String receiverId){
        return messageRepository.findMessagesWithoutSpecifiedInteraction(currentUserId, senderId, receiverId);
    }

    public List<Message> findChatAggr(String currentUserId, String currentUserSenderId, String friendSenderId, String currentUserReceiverId, String friendReceiverId){
        return messageRepository.findChat(currentUserId, currentUserSenderId, friendSenderId, currentUserReceiverId, friendReceiverId);
    }

    public List<Message> findAllMessagesAggr(String id){
        return messageRepository.findAllMessages(id);
    }

    public List<Message> findMessageByTextGlobalAggr(String currentUserId, String text){
        return messageRepository.findMessageByTextGlobal(currentUserId, text);
    }

    public List<Message> findMessageByTextPerFriendAggr(String currentUserId, String currentUserSenderId, String friendSenderId, String currentUserReceiverId, String friendReceiverId, String text){
        return messageRepository.findMessageByTextPerFriend(currentUserId, currentUserSenderId, friendSenderId, currentUserReceiverId, friendReceiverId, text);
    }

    public Post savePost(Post post){
        return postRepository.save(post);
    }

    public Optional<Post> findPostById(String postId){
        return postRepository.findById(postId);
    }

    public void deletePost(String postId){
        postRepository.deleteById(postId);
    }

    public List<Comment> findCommentListWithoutSpecifiedOneAggr(String postId, String commentId) {
        return postRepository.findCommentListWithoutSpecifiedOne(postId, commentId);
    }

    public List<Post> findAllPostsByArrAggr(Set<String> postsIds){
        return postRepository.findAllPostsByIdsArr(postsIds);
    }

    /**
     * da implementare
     */
    public Optional<User> findUserPostAggr(String postId){
        return userRepository.findUserPost(postId);
    }

    /**
    * non usate
     */
    public void deleteUserByEmail(String email){
        userRepository.deleteByEmail(email);
    }

    public Set<UserCountPerCity> countUsersPerCityAggregation(){
        return userRepository.countUsersPerCity();
    }
}