package com.example.demo.repository;

import com.example.demo.model.UserCountPerCity;
import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> deleteByEmail(String email);

    @Query("{'username': {$in: ?0}}")
    Optional<List<User>> findFriendsByUsername(List<String> friendsUsernames);

    @Aggregation(
            pipeline = {"{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    List<UserCountPerCity> countUsersPerCity();

    @Aggregation(
            pipeline = {"{$match: {'username': {$in: ?0}}}", "{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    List<UserCountPerCity> countFriendsPerCity(List<String> friendsUsernames);
}
