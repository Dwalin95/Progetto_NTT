package com.example.ntt.repository;

import com.example.ntt.model.Message;
import com.example.ntt.model.Post;
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

    void deleteByEmail(String email);

    @Query("{'_id': {$in: ?0}}")
    Optional<Set<User>> findFriendsById(Set<String> friendsIds);

    //TODO: dto
    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: 0, messages: 1}}", "{$match: {\"messages.senderId\": ?1, \"messages.receiverId\": ?2}}"}
    )
    List<Message> findChat(String username, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {_id: ObjectId(?1)}}"}
    )
    Message findSingleMessage(String username, String messageId);

    //findMessageByText

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$match: {\"posts._id\": ObjectId(?1)}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}"}
    )
    Post findSinglePost(String id, String postId);

    @Aggregation(
            pipeline = {"\"_id\": {$in: [?0]}", "{$unwind: {path: \"$posts\"}}", "{$match: {\"posts._id\": ObjectId(?1)}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}"}
    )
    List<Post> findAllPostsByArray(Set<User> friends);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}"}
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
