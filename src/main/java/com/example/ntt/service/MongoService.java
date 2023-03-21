package com.example.ntt.service;

import com.example.ntt.dto.EmailGenderOnlyDTO;
import com.example.ntt.model.Message;
import com.example.ntt.model.Post;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
import com.example.ntt.projections.UserContactInfoProjection;
import com.example.ntt.projections.UserFriendsAndRequestReceivedList;
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

    public Optional<User> findUserById(String id){
        return userRepository.findById(id);
    }
    public Optional<UserContactInfoProjection> getUserContactInfoByUsernameProjection(String username){
        return this.userRepository.findByUsername(username, UserContactInfoProjection.class);
    }

    public Optional<EmailGenderOnlyDTO> getUserEmailGender(String username) {
        return this.userRepository.findByUsername(username, EmailGenderOnlyDTO.class);
    }

    public Optional<UserFriendsAndRequestReceivedList> getFriendsAndRequestReceived(String username) {
        return this.userRepository.findByUsername(username, UserFriendsAndRequestReceivedList.class);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public Optional<Set<User>> findUserFriendsById(Set<String> friendsIds){
        return userRepository.findFriendsById(friendsIds);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public void deleteUserById(String id){
        userRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email){
        userRepository.deleteByEmail(email);
    }

    public List<Message> findSingleMessageAggregation(String username, String messageId){
        return userRepository.findSingleMessage(username, messageId);
    }

    public List<Post> getPostListWithoutSpecifiedMessageAggregation(String id, String postId){
        return userRepository.getPostListWithoutSpecifiedMessage(id, postId);
    }

    public List<Message> findMessagesWithoutSpecifiedInteraction(String username, String senderId, String receiverId){
        return userRepository.findMessagesWithoutSpecifiedInteraction(username, senderId, receiverId);
    }

    public List<Post> getPostListWithoutSpecifiedPost(String currentUserId, String postId){
        return userRepository.getPostListWithoutSpecifiedPost(currentUserId, postId);
    }

    public Post updatedPost(String currentUserId, String postId, String title, String body){
        return userRepository.updatedPost(currentUserId, postId, title, body);
    }

    public List<Message> findChatAggregation(String username, String senderId, String receiverId){
        return userRepository.findChatBySide(username, senderId, receiverId);
    }

    public List<Post> findAllPostsByArrayAggregation(Set<User> friends){
        return userRepository.findAllPostsByFriendIdsArr(friends);
    }

    public List<Message> findAllMessagesAggregation(String id){
        return userRepository.findAllMessagesBetweenUserAndFriendBySide(id);
    }

    public Set<UserCountPerCity> countUsersPerCityAggregation(){
        return userRepository.countUsersPerCity();
    }

    public Set<UserCountPerCity> countFriendsPerCityAggregation(Set<String> friendsUsernames){
        return userRepository.countFriendsPerCity(friendsUsernames);
    }
}