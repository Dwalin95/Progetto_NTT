package com.example.ntt.service;

import com.example.ntt.dto.*;
import com.example.ntt.exceptionHandler.PreconditionFailedException;
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
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userId.getId())));
        return messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());
    }

    public List<Message> findMessagesByFriendIds(CurrentUserIdAndFriendIdDTO userIds) {
        List<Message> chat = new ArrayList<>();
        chat.addAll(mongoService.findChatBySideAggregation(userIds.getCurrentUserId(), userIds.getFriendId(), userIds.getCurrentUserId()));
        chat.addAll(mongoService.findChatBySideAggregation(userIds.getCurrentUserId(), userIds.getCurrentUserId(), userIds.getFriendId()));
        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<Message> findMessageByTextGlobal(String currentUserId, String text){
        return mongoService.findUserById(currentUserId)
                .map(u -> mongoService.findMessageByTextGlobalAggregation(u.get_id(), text))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    public List<Message> findMessageByTextPerFriend(String currentUserId, String friendId, String text){
        List<Message> chat = new ArrayList<>();
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(currentUserId, currentUserId, friendId, text));
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(currentUserId, friendId, currentUserId, text));
        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp)).collect(Collectors.toList());
    }

    //TODO: Aggiungi MessageSentIdsDTO messageSent
    public void deleteSentMessage(String currentUserId, String friendId, String messageId){
        if(this.compareDatesForTimeLimit(currentUserId, messageId)) {
            this.deleteMessageAndSaveUser(currentUserId, messageId);
            this.deleteMessageAndSaveUser(friendId, messageId);
        } else {
            this.deleteMessageAndSaveUser(currentUserId, messageId);
            throw new PreconditionFailedException("Messages sent more than an hour ago cannot be deleted for both users, it was deleted only for you");
        }
    }

    //TODO: valutare se lasciare la logica nel backend o spostarla nel frontend
    private boolean compareDatesForTimeLimit(String currentUserId, String messageId){
        Message messageToCheck = mongoService.findSingleMessage(currentUserId, messageId);

        Calendar dateOfTheMessage = Calendar.getInstance();
        dateOfTheMessage.setTime(messageToCheck.getTimestamp());
        dateOfTheMessage.add(Calendar.HOUR_OF_DAY, 1);

        Date orarioAttuale = new Date();
        Calendar now = Calendar.getInstance();
        dateOfTheMessage.setTime(orarioAttuale);

        return now.after(dateOfTheMessage);
    }

    //TODO: aggiungi DTO MessageIdsDTO deleteMessage
    public void deleteReceivedMessage(String currentUserId, String messageId){
        this.deleteMessageAndSaveUser(currentUserId, messageId);
    }


    private void deleteMessageAndSaveUser(String currentUserId, String messageId) {
        mongoService.findUserById(currentUserId)
                .map(u -> this.removeMessage(u, messageId))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    private User removeMessage(User u, String messageId){
        List<Message> userMessage = mongoService.getMessageListWithoutSpecifiedMessage(u.getUsername(), messageId);
        u.setMessages(userMessage);
        return u;
    }

    //TODO: aggiungi CurrentUserIdAndFriendIdDTO userIds
    public void deleteChat(String currentUserId, String friendId){
        mongoService.findUserById(currentUserId)
                .map(u -> this.handleRemoveChat(friendId, u))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    private User handleRemoveChat(String friendId, User u) {
        this.updateMessageList(u, friendId, u.get_id());
        this.updateMessageList(u, u.get_id(), friendId);
        return u;
    }

    private void updateMessageList(User u, String senderId, String receiverId) {
        List<Message> chat = mongoService.findMessagesWithoutSpecifiedInteraction(u.get_id(), senderId, receiverId);
        u.setMessages(chat);
        mongoService.saveUser(u);
    }

    //TODO: controllare - FC
    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend) {
        User user = mongoService.findUserById(messageToSend.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, messageToSend.getCurrentUserId())));
        User messageReceiver = mongoService.findUserById(messageToSend.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, messageToSend.getFriendId())));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(messageToSend.getCurrentUserId())){ //TODO: check from here - FC

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(messageToSend.getBody())
                    .receiverId(messageToSend.getFriendId())
                    .senderId(messageToSend.getCurrentUserId())
                    .timestamp(new Date())
                    .build();

            mongoService.findUserById(currentUserId)
                    .map(u -> this.addMessage(message, u))
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
            this.addMessage(message, messageReceiver);
        } else {
            throw new UnauthorizedException("You can only send messages between friends");
        }
    }

    private User addMessage(Message message, User u) {
        u.getMessages().add(message);
        mongoService.saveUser(u);
        return u;
    }
}
