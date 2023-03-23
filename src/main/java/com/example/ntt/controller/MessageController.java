package com.example.ntt.controller;

import com.example.ntt.api.MessageApi;
import com.example.ntt.dto.*;
import com.example.ntt.model.Message;
import com.example.ntt.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
public class MessageController implements MessageApi {

    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<Set<String>> findAllMessageSenders(@RequestBody UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findAllMessageSenders(userId));
    }

    @Override
    public ResponseEntity<List<Message>> userMessagesByFriendId(@RequestBody CurrentUserIdAndFriendIdDTO userIds) {
        return ResponseEntity.ok(applicationService.findMessagesByFriendIds(userIds));
    }

    //TODO: DTO - FC
    @Override
    public ResponseEntity<List<Message>> findMessageByTextGlobal(String currentUserId, String text) {
        return ResponseEntity.ok(applicationService.findMessageByTextGlobal(currentUserId, text));
    }

    @Override
    public ResponseEntity<List<Message>> findMessageByTextPerFriend(String currentUserId, String friendId, String text) {
        return ResponseEntity.ok(applicationService.findMessageByTextPerFriend(currentUserId, friendId, text));
    }

    @Override
    public void deleteSentMessage(@RequestBody MessageSentIdsDTO messageSent) {
        applicationService.deleteSentMessage(messageSent);
    }
    @Override
    public void deleteReceivedMessage(@RequestBody MessageIdsDTO deleteMessage) { //currentUserId, messageId;
        applicationService.deleteReceivedMessage(deleteMessage);
    }

    @Override
    public void deleteChat(@RequestBody CurrentUserIdAndFriendIdDTO userIds) {
        applicationService.deleteChat(userIds);
    }
    @Override
    public void sendMessage(@RequestBody MessageToSendIdsAndBodyDTO messageToSend) {
        applicationService.sendMessage(messageToSend);
    }
}
