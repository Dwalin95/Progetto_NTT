package com.example.ntt.service;

import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MongoService mongoService;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public Set<String> findAllMessageSenders(String id){
        List<Message> messages = mongoService.findUserById(id)
                .map(u -> mongoService.findAllMessagesAggregation(u.get_id()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
        return messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());
    }

    //TODO: da testare
    public List<Message> findMessagesByFriendIds(String id, String friendId) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        User friend = mongoService.findUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));

        List<Message> messagesFromUser = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), user.get_id(), friendId);
        List<Message> messagesToUser = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), friend.get_id(), user.get_id());

        List<Message> chat = new ArrayList<>();
        chat.addAll(messagesToUser);
        chat.addAll(messagesFromUser);

        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    //TODO: da testare
    public void deleteMessage(String id, String friendId, String messageId){
        this.handleDeleteMessage(id, messageId);
        this.handleDeleteMessage(friendId, messageId);
    }

    private void handleDeleteMessage(String id, String messageId) {
        mongoService.findUserById(id)
                .map(u -> this.removeMessage(u, messageId))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User removeMessage(User u, String messageId){
        Message userMessage = mongoService.findMessageAggregation(u.getUsername(), messageId);
        u.getMessages().remove(userMessage);
        return u;
    }

    //TODO: da testare
    public void deleteChat(String id, String friendId){
        mongoService.findUserById(id)
                .map(u -> this.handleRemoveChat(friendId, u))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
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
    public void sendMessage(String id, String friendId, String body) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(user.get_id())){

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(body)
                    .receiverId(friendId)
                    .senderId(id)
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
