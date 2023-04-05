package com.example.ntt.service;

import com.example.ntt.dto.user.EmailGenderOnlyDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.*;
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

    public List<Message> findChatBySideAggr(String currentUserId, String senderId, String receiverId){
        return messageRepository.findChatBySide(currentUserId, senderId, receiverId);
    }

    public List<Message> findAllMessagesAggr(String id){
        return messageRepository.findAllMessages(id);
    }

    public List<Message> findMessageByTextGlobalAggr(String currentUserId, String text){
        return messageRepository.findMessageByTextGlobal(currentUserId, text);
    }

    public List<Message> findMessageByTextPerFriendBySideAggr(String currentUserId, String senderId, String receivedId, String text){
        return messageRepository.findMessageByTextPerFriendBySide(currentUserId, senderId, receivedId, text);
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

    //TODO: refuso(?) //TODO: FC - nel merge non mi ha dato nulla, post merge questo metodo mi dà errore. Push del 05.04.2023
//    public Post updatedPostAggregation(String currentUserId, String postId, String title, String body){
//        return postRepository.updatedPost(currentUserId, postId, title, body);
//    }

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

    public Post updatedPostAggregation(String currentUserId, String postId, String title, String body){
        return postRepository.updatedPost(currentUserId, postId, title, body);
    }
}