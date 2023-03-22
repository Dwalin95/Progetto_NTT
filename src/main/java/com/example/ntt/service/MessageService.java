package com.example.ntt.service;

import com.example.ntt.exceptionHandler.PreconditionFailedException;
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
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
        return messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());
    }

    public List<Message> findMessagesByFriendIds(String currentUserId, String friendId) {
        List<Message> chat = new ArrayList<>();
        chat.addAll(mongoService.findChatBySideAggregation(currentUserId, friendId, currentUserId));
        chat.addAll(mongoService.findChatBySideAggregation(currentUserId, currentUserId, friendId));
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

    public void sendMessage(String currentUserId, String friendId, String body) {
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));
        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(currentUserId)){

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(body)
                    .receiverId(friendId)
                    .senderId(currentUserId)
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
