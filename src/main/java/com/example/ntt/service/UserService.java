package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.exceptionHandler.UnauthorizedException;
import com.example.ntt.model.Message;
import com.example.ntt.model.User;
import com.example.ntt.model.UserCountPerCity;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public ResponseEntity<User> updatePasswordByIdService(String id, String oldPassword, String confirmedPassword) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));

        if (userConfiguration.passwordEncoder().matches(oldPassword, user.getPwz())) {
            if (!userConfiguration.passwordEncoder().matches(confirmedPassword, user.getPwz())) {
                user.setPwz(userConfiguration.passwordEncoder().encode(confirmedPassword));
                mongoService.saveUser(user);
                return ResponseEntity.ok(user);
            } else {
                throw new UnauthorizedException("New password can't be equal to the old password");
            }
        } else {
            throw new ResourceNotFoundException("Entered characters do not match the old password");
        }
    }

    public User findUserById(String id) {
        return mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    public Set<User> findFriendsById(String id) {
        return mongoService.findUserById(id)
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)))
                        .orElse(new HashSet<User>());
    }

    //TODO: da completare
    public Set<String> findAllMessageSendersService(String id){
        Optional<User> user = mongoService.findUserById(id);
        user.map(u -> mongoService.findAllMessagesAggregation(u.get_id()));
        //List<Message> messages = mongoService.findAllMessagesAggregation(user.get_id());
        /*return messages.stream()
                                    .map(Message::getSenderId)
                                    .collect(Collectors.toSet());*/
        return new HashSet<>();
    }

    //TODO: vedere perché ritorna tutto null
    public List<Message> findMessagesByFriendIdsService(String id, String friendId) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format(USER_NOT_FOUND_ERROR_MSG, id)));
        User friend = mongoService.findUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format(USER_NOT_FOUND_ERROR_MSG, id)));
        List<Message> messagesToUser = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), friend.get_id(), user.get_id());
        List<Message> messagesFromUser = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), user.get_id(), friendId);
        List<Message> chat = new ArrayList<>();
        chat.addAll(messagesToUser);
        chat.addAll(messagesFromUser);
        /*return ResponseEntity.ok(friendMessages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList()));*/
        return chat;
    }

    public Set<UserCountPerCity> friendsCountPerCity(String id) {
        Optional<User> user = mongoService.findUserById(id);
        return user.map(User::getFriends)
                    .map(mongoService::countFriendsPerCityAggregation)
                    .orElseThrow(() -> new ResourceNotFoundException("No friends found"));
    }

    public Set<User> findUserReceivedFriendRequestsById(String id) {
        Optional<User> user = mongoService.findUserById(id);
        return user.map(User::getReceivedFriendRequests)
                    .map(mongoService::findUserFriendsById)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)))
                    .orElse(new HashSet<>());
    }

    public ResponseEntity<Set<User>> findUserSentFriendRequestByIdService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        Set<String> sentFriendRequestsList = user.getSentFriendRequests();
        Set<User> users = mongoService.findUserFriendsById(sentFriendRequestsList).orElse(new HashSet<>());
        return ResponseEntity.ok(users);
    }

    public User updateUserById(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender) {
        Optional<User> user = mongoService.findUserById(id);
        return user.map(u -> {
                mongoService.saveUser(u.withUsername(username.orElse(u.getUsername()))
                        .withFirstName(firstName.orElse(u.getFirstName()))
                        .withLastName(lastName.orElse(u.getLastName()))
                        .withEmail(email.orElse(u.getEmail()))
                        .withGender(gender.orElse(u.getGender())));
                return u;
                }).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    public void sendFriendRequest(String id, String friendId){
        this.handleRequest(friendId, user -> user.getReceivedFriendRequests().add(id));
        this.handleRequest(id, user -> user.getSentFriendRequests().add(friendId));
    }

    private void handleRequest(String id, Predicate<User> isRequestAdded){
        mongoService.findUserById(id)
                .filter(isRequestAdded)
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    public void handleFriendRequest(String id, String friendId, boolean accepted){
        this.handleSingleFriendRequest(id, friendId, accepted);
        this.handleSingleFriendRequest(friendId, id, accepted);
    }

    private void handleSingleFriendRequest(String id, String friendId, boolean accepted) {
        mongoService.findUserById(id)
                    .map(u -> this.removeRequest(friendId, u))
                    .filter(u -> accepted)
                    .ifPresent(u -> u.getFriends().add(friendId));
        mongoService.findUserById(id)
                    .map(mongoService::saveUser)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User removeRequest(String friendId, User u) {
        u.getReceivedFriendRequests().remove(friendId);
        return u;
    }

    //da decidere se cancellarli a tutti e due o solo chi li vuole cancellare, la chat intera viene rimossa solo a chi fa l'azione non a tutti e due
    //TODO: controllare se cancella il messaggio
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

    //TODO: perchè non cancella le chat???
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

    public void removeFriendService(String id, String friendId){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        User friend = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        mongoService.saveUser(user);
        mongoService.saveUser(friend);
    }

    //TODO: da trasformare con la funzionale
    public void sendMessage(String id, String friendId, String body) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendId)));

        Set<String> friends = messageReceiver.getFriends();

        if(friends.contains(user.get_id())){

            Message message = Message.builder()
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
