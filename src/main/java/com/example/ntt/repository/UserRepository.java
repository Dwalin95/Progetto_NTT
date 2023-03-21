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

    //Test Projection
    <T> Optional<T> findByUsername(String username, Class<T> type); //TODO: test

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    void deleteByEmail(String email);

    @Query("{'_id': {$in: ?0}}")
    Optional<Set<User>> findFriendsById(Set<String> friendsIds);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {messages.senderId: {$ne: ObjectId(?1)}, messages.receiverId: {$ne: ObjectId(?2)}}"}
    )
    List<Message> findMessagesWithoutSpecifiedInteraction(String username, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {\"messages.senderId\": ObjectId(?1), \"messages.receiverId\": ObjectId(?2)}"}
    )
    List<Message> findChatBySide(String username, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {_id: {$ne: ObjectId(?1)}}}"}
    )
    List<Message> findSingleMessage(String username, String messageId);

    //findMessageByText

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$match: {\"posts._id\": {$ne: ObjectId(?1)}}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}"}
    )
    List<Post> getPostListWithoutSpecifiedMessage(String id, String postId);

    @Aggregation(
            pipeline = {"\"_id\": {$in: [?0]}", "{$unwind: {path: \"$posts\"}}", "{$match: {\"posts._id\": ObjectId(?1)}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}"}
    )
    List<Post> findAllPostsByFriendIdsArr(Set<User> friends);

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}", "{$match: {_id: {$ne: ObjectId(?0)}}}"}
    )
    List<Post> getPostListWithoutSpecifiedPost(String currentUserId, String postId);

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}", "{$match: {_id: ObjectId(?1)}}", "{$set: {title: ?2, body: ?3, modified: true}}"}
    )
    Post updatedPost(String currentUserId, String postId, String title, String body);


    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}"}
    )
    List<Message> findAllMessagesBetweenUserAndFriendBySide(String id);

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
