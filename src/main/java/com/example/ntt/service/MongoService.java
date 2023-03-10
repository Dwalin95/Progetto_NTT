package com.example.ntt.service;

import com.example.ntt.model.Message;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
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
        return this.userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email){
        return this.userRepository.findByEmail(email);
    }

    public Optional<User> findUserByUsername(String username){
        return this.userRepository.findByUsername(username);
    }

    public Optional<Set<User>> findUserFriendsById(Set<String> friendsIds){
        return this.userRepository.findFriendsById(friendsIds);
    }

    public List<User> findAllUsers(){
        return this.userRepository.findAll();
    }

    public User saveUser(User user){
        return this.userRepository.save(user);
    }

    public void deleteUserById(String id){
        this.userRepository.deleteById(id);
    }

    public void deleteUserByEmail(String email){
        this.userRepository.deleteByEmail(email);
    }

    public List<Message> findAllMessageAggregations(String id){
        return this.userRepository.findAllMessage(id);
    }

    public List<Message> findMessagesByFriendIdAggregation(String username, String friendId){
        return this.userRepository.findMessagesByFriendId(username, friendId);
    }

    public List<UserCountPerCity> countUsersPerCityAggregation(){
        return this.userRepository.countUsersPerCity();
    }

    public Set<UserCountPerCity> countFriendsPerCityAggregation(Set<String> friendsUsernames){
        return this.userRepository.countFriendsPerCity(friendsUsernames);
    }
}