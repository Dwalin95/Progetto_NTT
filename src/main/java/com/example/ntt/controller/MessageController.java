package com.example.ntt.controller;

import com.example.ntt.api.MessageApi;
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
    public ResponseEntity<Set<String>> findAllMessageSenders(String currentUserId) {
        return ResponseEntity.ok(applicationService.findAllMessageSenders(currentUserId));
    }

    @Override
    public ResponseEntity<List<Message>> findUserMessagesByFriendId(String currentUserId, String friendId) {
        return ResponseEntity.ok(applicationService.findMessagesByFriendIds(currentUserId, friendId));
    }

    @Override
    public ResponseEntity<List<Message>> findMessageByTextGlobal(String currentUserId, String text) {
        return ResponseEntity.ok(applicationService.findMessageByTextGlobal(currentUserId, text));
    }

    @Override
    public ResponseEntity<List<Message>> findMessageByTextPerFriend(String currentUserId, String friendId, String text) {
        return ResponseEntity.ok(applicationService.findMessageByTextPerFriend(currentUserId, friendId, text));
    }

    @Override
    public void deleteSentMessage(String currentUserId, String friendId, String messageId) {
        applicationService.deleteSentMessage(currentUserId, friendId, messageId);
    }
    @Override
    public void deleteReceivedMessage(String currentUserId, String messageId) {
        applicationService.deleteReceivedMessage(currentUserId, messageId);
    }

    @Override
    public void deleteChat(String currentUserId, String friendId) {
        applicationService.deleteChat(currentUserId, friendId);
    }
    @Override
    public void sendMessage( String currentUserId, String friendId, String body) {
        applicationService.sendMessage(currentUserId, friendId, body);
    }
}
