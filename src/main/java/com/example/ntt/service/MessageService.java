package com.example.ntt.service;

import com.example.ntt.dto.*;
import com.example.ntt.enums.ErrorMsg;
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
    private final MongoClient mongoClient;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found"; //TODO: spostare in un ENUM

    public Set<String> findAllMessageSenders(UserIdDTO userId){
        List<Message> messages = mongoService.findUserById(userId.getId())
                .map(u -> mongoService.findAllMessagesAggregation(u.get_id()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())));
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

    //TODO: DTO - FC
    public List<Message> findMessageByTextGlobal(String currentUserId, String text){
        return mongoService.findUserById(currentUserId)
                .map(u -> mongoService.findMessageByTextGlobalAggregation(u.get_id(), text))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), currentUserId)));
    }

    //TODO: DTO - FC
    public List<Message> findMessageByTextPerFriend(String currentUserId, String friendId, String text){
        List<Message> chat = new ArrayList<>();
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(currentUserId, currentUserId, friendId, text));
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(currentUserId, friendId, currentUserId, text));
        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp)).collect(Collectors.toList());
    }

    //TODO: Aggiungi MessageSentIdsDTO messageSent - FC
    public void deleteSentMessage(MessageSentIdsDTO messageSent){
        if(this.compareDatesForTimeLimit(messageSent.getCurrentUserId(), messageSent.getMessageId())) {
            this.deleteMessageAndSaveUser(messageSent.getCurrentUserId(), messageSent.getMessageId());
            this.deleteMessageAndSaveUser(messageSent.getFriendId(), messageSent.getMessageId());
        } else {
            this.deleteMessageAndSaveUser(messageSent.getCurrentUserId(), messageSent.getMessageId());
            throw new PreconditionFailedException(ErrorMsg.DELETE_MESSAGE_TIMEOUT.getMsg());
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

    //TODO: aggiungi DTO MessageIdsDTO deleteMessage - FC
    public void deleteReceivedMessage(MessageIdsDTO deleteMessage){ //TODO: vedere con Pier il metodo [passaggio del DTO] - FC
        this.deleteMessageAndSaveUser(deleteMessage.getCurrentUserId(), deleteMessage.getMessageId());
    }


    private void deleteMessageAndSaveUser(String currentUserId, String messageId) {
        mongoService.findUserById(currentUserId)
                .map(u -> this.removeMessage(u, messageId))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), currentUserId)));
    }

    private User removeMessage(User u, String messageId){
        List<Message> userMessage = mongoService.getMessageListWithoutSpecifiedMessage(u.getUsername(), messageId);
        u.setMessages(userMessage);
        return u;
    }

    //TODO: aggiungi CurrentUserIdAndFriendIdDTO userIds - FC
    public void deleteChat(CurrentUserIdAndFriendIdDTO userIds){
        mongoService.findUserById(userIds.getCurrentUserId())
                .map(u -> this.handleRemoveChat(userIds.getFriendId(), u))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userIds.getCurrentUserId())));
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
        User messageReceiver = mongoService.findUserById(messageToSend.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), messageToSend.getFriendId())));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(messageToSend.getCurrentUserId())){ //TODO: check from here - FC

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(messageToSend.getBody())
                    .receiverId(messageToSend.getFriendId())
                    .senderId(messageToSend.getCurrentUserId())
                    .timestamp(new Date())
                    .build();

            mongoService.findUserById(messageToSend.getCurrentUserId())
                    .map(u -> this.addMessage(message, u))
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), messageToSend.getCurrentUserId())));
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
