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
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found"; //TODO: spostare in un ENUM

    public Set<String> findAllMessageSenders(String currentUserId){
        List<Message> messages = mongoService.findUserById(currentUserId)
                .map(u -> mongoService.findAllMessagesAggregation(u.get_id()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", currentUserId)));
        return messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());
    }

    //TODO: PERCHE' RITORNA NUUUUULLLL
    public List<Message> findMessagesByFriendIds(String currentUserId, String friendId) {
        User user = mongoService.findUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
        User friend = mongoService.findUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));

        List<Message> messagesFromUser = mongoService.findChatAggregation(user.getUsername(), user.get_id(), friendId);
        List<Message> messagesToUser = mongoService.findChatAggregation(user.getUsername(), friend.get_id(), user.get_id());

        List<Message> chat = new ArrayList<>();
        chat.addAll(messagesToUser);
        chat.addAll(messagesFromUser);

        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    //TODO: non cancella il messaggio
    public void deleteSentMessage(String currentUserId, String friendId, String messageId){
        this.deleteMessageAndSaveUser(currentUserId, messageId);
        this.deleteMessageAndSaveUser(friendId, messageId);
    }

    //TODO: non cancella il messaggio
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
        Message userMessage = mongoService.findSingleMessageAggregation(u.getUsername(), messageId);
        u.getMessages().remove(userMessage);
        return u;
    }

    //TODO: non cancella il messaggio
    public void deleteChat(String currentUserId, String friendId){
        mongoService.findUserById(currentUserId)
                .map(u -> this.handleRemoveChat(friendId, u))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
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
    public void sendMessage(String currentUserId, String friendId, String body) {
        User user = mongoService.findUserById(currentUserId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(user.get_id())){

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(body)
                    .receiverId(friendId)
                    .senderId(currentUserId)
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
