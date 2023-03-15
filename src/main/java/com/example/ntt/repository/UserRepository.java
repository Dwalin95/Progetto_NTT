package com.example.ntt.repository;

import com.example.ntt.model.Message;
import com.example.ntt.model.UserCountPerCity;
import com.example.ntt.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> deleteByEmail(String email);

    @Query("{'_id': {$in: ?0}}")
    Optional<Set<User>> findFriendsById(Set<String> friendsIds);

    //TODO: dto
    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: 0, messages: 1}}", "{$match: {\"messages.senderId\": ?1, \"messages.receiverId\": ?2}}"}
    )
    List<Message> findChat(String username, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: 0, messages: 1}}", "{$match: {\"messages._id\": ObjectId(?1)}}"}
    )
    Message findMessage(String username, String messageId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: 0, messages: 1}}"}
    )
    List<Message> findAllMessages(String id);

    @Aggregation(
            pipeline = {"{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    Set<UserCountPerCity> countUsersPerCity();

    //TODO da ricontrollare
    @Aggregation(
            pipeline = {"{$match: {'_id': {$in: ?0}}}", "{$group: {_id: \"$address.city\", numUsers: {$sum: 1}}}"}
    )
    Set<UserCountPerCity> countFriendsPerCity(Set<String> friendsIds);
}
