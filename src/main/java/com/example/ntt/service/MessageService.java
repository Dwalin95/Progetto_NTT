package com.example.ntt.service;

import com.example.ntt.dto.*;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
//import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MongoService mongoService;
    private final MongoClient mongoClient;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found"; //TODO: spostare in un ENUM

    public Set<String> findAllMessageSenders(UserIdDTO userId){
        List<Message> messages = mongoService.findUserById(userId.getId())
                .map(u -> mongoService.findAllMessagesAggregation(u.get_id()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", userId.getId())));
        return messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());
    }

    //TODO: PERCHE' RITORNA NUUUUULLLL
    public List<Message> findMessagesByFriendIds(CurrentUserIdAndFriendIdDTO userIds) {
        User user = mongoService.findUserById(userIds.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userIds.getCurrentUserId())));
        User friend = mongoService.findUserById(userIds.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userIds.getFriendId())));

        List<Message> messagesFromUser = mongoService.findChatAggregation(user.getUsername(), user.get_id(), userIds.getFriendId());
        List<Message> messagesToUser = mongoService.findChatAggregation(user.getUsername(), friend.get_id(), user.get_id());

        List<Message> chat = new ArrayList<>();
        chat.addAll(messagesToUser);
        chat.addAll(messagesFromUser);

        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    //TODO: non cancella il messaggio
    public void deleteSentMessage(MessageSentIdsDTO messageSent){
        this.deleteMessageAndSaveUser(messageSent.getCurrentUserId(), messageSent.getMessageId());
        this.deleteMessageAndSaveUser(messageSent.getFriendId(), messageSent.getMessageId());
    }

    //TODO: non cancella il messaggio
    public void deleteReceivedMessage(MessageIdsDTO deleteMessage){
        this.deleteMessageAndSaveUser(deleteMessage.getCurrentUserId(), deleteMessage.getMessageId());
    }

    private void deleteMessageAndSaveUser(String currentUserId, String messageId) {
        mongoService.findUserById(currentUserId)
                .map(u -> this.removeMessage(u, messageId))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    private User removeMessage(User u, String messageId){
        Message userMessage = mongoService.findSingleMessageAggregation(u.getUsername(), messageId);

        List<Document> userMessages = Arrays.asList(new Document("$match",
                        new Document("_id", new ObjectId(u.get_id()))),
                new Document("$unwind",
                        new Document("path", "$messages")),
                new Document("$project",
                        new Document("_id", "$messages._id")
                                .append("body", "$messages.body")
                                .append("timestamp", "$messages.timestamp")
                                .append("senderId", "$messages.senderId")
                                .append("receiverId", "$messages.receiverId")),
                new Document("$match",
                        new Document("_id",
                                new ObjectId(messageId))));

        MongoDatabase database = mongoClient.getDatabase("Task_Force");
        MongoCollection<Document> collection = database.getCollection("users");

        //collection.deleteOne(userMessage);
        //u.getMessages().remove(userMessage);
        return u;
    }

    //TODO: non cancella il messaggio
    public void deleteChat(CurrentUserIdAndFriendIdDTO userIds){
        mongoService.findUserById(userIds.getCurrentUserId())
                .map(u -> this.handleRemoveChat(userIds.getFriendId(), u))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userIds.getCurrentUserId())));
    }

    private User handleRemoveChat(String friendId, User u) {
        this.removeChatBySide(u, friendId, u.get_id());
        this.removeChatBySide(u, u.get_id(), friendId);
        return u;
    }

    private void removeChatBySide(User u, String senderId, String receiverId) {
        List<Message> chat = mongoService.findChatAggregation(u.getUsername(), senderId, receiverId);
        u.getMessages().removeAll(chat);
    }

    //TODO: da trasformare in funzionale
    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend) {
        User user = mongoService.findUserById(messageToSend.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, messageToSend.getCurrentUserId())));
        User messageReceiver = mongoService.findUserById(messageToSend.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, messageToSend.getFriendId())));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(user.get_id())){

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(messageToSend.getBody())
                    .receiverId(messageToSend.getFriendId())
                    .senderId(messageToSend.getCurrentUserId())
                    .timestamp(new Date())
                    .build();

            user.getMessages().add(message);
            messageReceiver.getMessages().add(message);
            mongoService.saveUser(user);
            mongoService.saveUser(messageReceiver);
        } else {
            throw new UnauthorizedException("You can only send messages between friends");
        }
    }
}
