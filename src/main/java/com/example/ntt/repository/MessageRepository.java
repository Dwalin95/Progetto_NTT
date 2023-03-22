package com.example.ntt.repository;

import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<User, String> {

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {senderId: {$ne: ?1}, receiverId: {$ne: ?2}}}"}
    )
    List<Message> findMessagesWithoutSpecifiedInteraction(String currentUserId, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {\"messages.senderId\": ObjectId(?1), \"messages.receiverId\": ObjectId(?2)}"}
    )
    List<Message> findChatBySide(String username, String senderId, String receiverId);

    @Aggregation(
            pipeline = {"{$match: {username: ?0}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {_id: {$ne: ObjectId(?1)}}}"}
    )
    List<Message> getMessageListWithoutSpecifiedMessage(String username, String messageId);

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}", "{$match: {_id: ObjectId(?1)}}"}
    )
    Message findSingleMessage(String currentUserId, String messageId);

    //findMessageByText

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$messages\"}}", "{$project: {_id: \"$messages._id\", body: \"$messages.body\", timestamp: \"$messages.timestamp\", senderId: \"$messages.senderId\", receiverId: \"$messages.receiverId\"}}"}
    )
    List<Message> findAllMessages(String currentUserId);
}
