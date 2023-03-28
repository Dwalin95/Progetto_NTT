package com.example.ntt.repository;

import com.example.ntt.model.PostAuthorAndId;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //Test Projection
    <T> Optional<T> findByUsername(String username, Class<T> type); //TODO: test

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    void deleteByEmail(String email);

    @Query("{'_id': {$in: ?0}}")
    Optional<Set<User>> findFriendsById(Set<String> friendsIds);

    <T> Optional<Set<T>> findFriendsById(Set<String> friendsIds, Class<T> type);

    @Aggregation(
            pipeline = {"{$unwind: {path: \"$posts\"}}", "{$match: {\"posts._id\": ObjectId(?0)}}"}
    )
    Optional<User> findUserPost(String postId);

    @Aggregation(
            pipeline = {"{$match: {'_id': {$in: ?0}}}", "{$unwind: {path: \"$postsIds\"}}", "{$project: {_id: 0, author: \"$_id\", postId: \"$postsIds\"}}"}
    )
    Set<PostAuthorAndId> findAllFriendsPostsIds(Set<String> friendsIds);

    @Aggregation(
            pipeline = {"{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    Set<UserCountPerCity> countUsersPerCity();

    @Aggregation(
            pipeline = {"{$match: {'_id': {$in: ?0}}}", "{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    Set<UserCountPerCity> countFriendsPerCity(Set<String> friendsIds);
}