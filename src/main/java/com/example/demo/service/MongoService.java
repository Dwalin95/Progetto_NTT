package com.example.demo.service;

import com.example.demo.model.UserCountPerCity;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<List<User>> findUserFriendsByUsername(List<String> friendsUsernames){
        return this.userRepository.findFriendsByUsername(friendsUsernames);
    }

    public Optional<List<User>> findAllUsers(){
        return Optional.of(this.userRepository.findAll());
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

    public List<UserCountPerCity> countUsersPerCityAggregation(){
        return this.userRepository.countUsersPerCity();
    }

    public List<UserCountPerCity> countFriendsPerCityAggregation(List<String> friendsUsernames){
        return this.userRepository.countFriendsPerCity(friendsUsernames);
    }
}