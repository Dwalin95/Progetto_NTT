package com.example.ntt.service;

import com.example.ntt.dto.message.*;
import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.PreconditionFailedException;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import com.mongodb.client.MongoClient;
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
        List<Message> friendSide = mongoService.findChatBySideAggregation(userIds.getCurrentUserId(), userIds.getFriendId(), userIds.getCurrentUserId());
        chat.addAll(friendSide);
        chat.addAll(mongoService.findChatBySideAggregation(userIds.getCurrentUserId(), userIds.getCurrentUserId(), userIds.getFriendId()));
        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<Message> findMessageByTextGlobal(MessageTextAndCurrentUserIdDTO messageByText){
        return mongoService.findUserById(messageByText.getCurrentUserId())
                .map(u -> mongoService.findMessageByTextGlobalAggregation(u.get_id(), messageByText.getText()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), messageByText.getCurrentUserId())));
    }

    public List<Message> findMessageByTextPerFriend(MessageTextAndCurrentUserAndFriendIdDTO messageByText){
        List<Message> chat = new ArrayList<>();
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(messageByText.getCurrentUserId(), messageByText.getCurrentUserId(), messageByText.getFriendId(), messageByText.getText()));
        chat.addAll(mongoService.findMessageByTextPerFriendBySideAggregation(messageByText.getCurrentUserId(), messageByText.getFriendId(), messageByText.getCurrentUserId(), messageByText.getText()));
        return chat.stream()
                .sorted(Comparator.comparing(Message::getTimestamp)).collect(Collectors.toList());
    }

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
        now.setTime(orarioAttuale);

        return now.before(dateOfTheMessage);
    }

    public void deleteReceivedMessage(MessageReceivedIdsDTO deleteMessage){ //TODO: vedere con Pier il metodo [passaggio del DTO] - FC
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

    //TODO: migliorare il codice - LDB
    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend) {
        User messageReceiver = mongoService.findUserById(messageToSend.getFriendId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), messageToSend.getFriendId())));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(messageToSend.getCurrentUserId())){

            Message message = Message.builder()
                    ._id(new ObjectId())
                    .body(messageToSend.getBody())
                    .senderId(messageToSend.getCurrentUserId())
                    .receiverId(messageToSend.getFriendId())
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
