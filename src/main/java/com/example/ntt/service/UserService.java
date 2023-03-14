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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public ResponseEntity<User> updatePasswordByIdService(String id, String oldPassword, String confirmedPassword) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

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

    public ResponseEntity<User> findUserByIdService(String id) {
        Optional<User> user = mongoService.findUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
    }

    public ResponseEntity<Set<User>> findFriendsByIdService(String id) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
        Set<User> friends = mongoService.findUserFriendsById(user.getFriends()).orElse(new HashSet<User>());
        return ResponseEntity.ok(friends);
    }

    //TODO: vedere perché torna un set vuoto
    public ResponseEntity<Set<String>> findAllMessageSendersService(String id){
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        List<Message> messages = mongoService.findAllMessageAggregations(user.get_id());
        return ResponseEntity.ok(messages.stream()
                                    .map(Message::getSenderId)
                                    .collect(Collectors.toSet()));
    }

    //TODO: vedere perché ritorna tutto null
    public ResponseEntity<List<Message>> findMessagesByFriendIdsService(String id, String friendId) {
        User user = mongoService.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        User friend = mongoService.findUserById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(String
                        .format("User with id: %s not found", id)));
        List<Message> friendMessages = mongoService.findMessagesByFriendIdAggregation(user.getUsername(), friend.get_id());
        return ResponseEntity.ok(friendMessages.stream()
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList()));
    }

    public ResponseEntity<List<User>> findAllUserService() {
        Optional<List<User>> userList = Optional.of(mongoService.findAllUsers());
        return userList.map(ResponseEntity::ok).orElseThrow(() -> new ResourceNotFoundException("No users found"));
    }

    public ResponseEntity<List<UserCountPerCity>> userCountPerCityService() {
        Optional<List<UserCountPerCity>> userCount = Optional.of(mongoService.countUsersPerCityAggregation());
        return userCount.map(ResponseEntity::ok).orElseThrow(()->new ResourceNotFoundException("No users found"));
    }

    public ResponseEntity<Set<UserCountPerCity>> friendsCountPerCityService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
        Set<String> count = user.getFriends();
        return ResponseEntity.ok(mongoService.countFriendsPerCityAggregation(count));
    }
    public ResponseEntity<Set<User>> findUserFriendRequestsByIdService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        Set<String> friendRequestsList = user.getReceivedFriendRequests();
        Set<User> users = mongoService.findUserFriendsById(friendRequestsList).orElse(new HashSet<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<Set<User>> findUserSentFriendRequestByIdService(String id) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        Set<String> sentFriendRequestsList = user.getSentFriendRequests();
        Set<User> users = mongoService.findUserFriendsById(sentFriendRequestsList).orElse(new HashSet<>());
        return ResponseEntity.ok(users);
    }

    public ResponseEntity<User> updateUserByIdService(String id, Optional<String> username, Optional<String> firstName, Optional<String> lastName, Optional<String> email, Optional<String> gender) {

        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        User updatedUser = mongoService.saveUser(user.withUsername(username.orElse(user.getUsername()))
                .withFirstName(firstName.orElse(user.getFirstName()))
                .withLastName(lastName.orElse(user.getLastName()))
                .withEmail(email.orElse(user.getEmail()))
                .withGender(gender.orElse(user.getGender())));

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Invia una richiesta di amicizia ad un altro utente
     * [currentUserid] -> [friendUserId]
     * @param currentUserId
     * @param friendUserId
     */
    public void sendFriendRequest(String currentUserId, String friendUserId) {

        //currentUser :: send friend request
        mongoService.findUserById(currentUserId)
                .map(currentUser -> this.addSentFriendRequest(friendUserId, currentUser))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));

        //friendUser :: receive friend request
        mongoService.findUserById(friendUserId)
                .map(friendUser -> this.addReceivedFriendRequest(currentUserId, friendUser))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, friendUserId)));

    }

    /**
     * Aggiunge nella lista delle richieste di amicizia inviate l'id dell'utente destinatario
     * @param friendUserId
     * @param currentUser
     * @return
     */
    private User addSentFriendRequest(String friendUserId, User currentUser) {
        currentUser.getSentFriendRequests().add(friendUserId);
        return currentUser;
    }

    /**
     * Aggiunge nella lista delle richieste di amicizia ricevute l'id dell'utente di provenienza
     * @param currentUserId
     * @param friendUser
     * @return
     */
    private User addReceivedFriendRequest(String currentUserId, User friendUser) {
        friendUser.getReceivedFriendRequests().add(currentUserId);
        return friendUser;
    }

    public void handleFriendRequest(String id, String friendId, boolean accepted){

        this.handleSingleFriendRequest(id, friendId, accepted);
        this.handleSingleFriendRequest(friendId, id, accepted);
    }

    private void handleSingleFriendRequest(String id, String friendId, boolean accepted) {
        Optional<User> user = mongoService.findUserById(id);

        user.map(u -> this.removeRequest(friendId, u))
            .filter(u -> accepted)
            .ifPresent(u -> u.getFriends().add(friendId));
        user.map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id: %s not found", id)));
    }

    private User removeRequest(String friendId, User u) {
        u.getReceivedFriendRequests().remove(friendId);
        return u;
    }

    //da decidere se cancellarli a tutti e due o solo chi li vuole cancellare, la chat intera viene rimossa solo a chi fa l'azione non a tutti e due
    public void deleteMessage(String id, String friendId, String messageId){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        User friend = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendId)));

        Message userMessage = mongoService.findMessageAggregation(user.getUsername(), messageId);
        Message friendMessage = mongoService.findMessageAggregation(friend.getUsername(), messageId);
        user.getMessages().remove(userMessage);
        friend.getMessages().remove(friendMessage);
        mongoService.saveUser(user);
        mongoService.saveUser(friend);
    }

    //TODO: perchè non cancella le chat???
    public void deleteChatService(String id, String friendId){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));

        List<Message> chatFriendSide = mongoService.findChatAggregation(user.getUsername(), friendId, user.get_id());
        List<Message> chatUserSide = mongoService.findChatAggregation(user.getUsername(), friendId, user.get_id());

        user.getMessages().removeAll(chatFriendSide);
        user.getMessages().removeAll(chatUserSide);
        mongoService.saveUser(user);
    }

    public void removeFriendService(String id, String friendId){
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        User friend = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendId)));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        mongoService.saveUser(user);
        mongoService.saveUser(friend);
    }

    public void sendMessageService(String id, String friendId, String body) {
        User user = mongoService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", id)));
        User messageReceiver = mongoService.findUserById(friendId).orElseThrow(() -> new ResourceNotFoundException(String.format("User: %s not found", friendId)));

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
