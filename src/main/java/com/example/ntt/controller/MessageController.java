package com.example.ntt.controller;

import com.example.api.MessageApi;
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
    public ResponseEntity<Set<String>> findAllMessageSenders(String id) {
        return ResponseEntity.ok(applicationService.findAllMessageSenders(id));
    }

    @Override
    public ResponseEntity<List<Message>> findUserMessagesByFriendId(String id, String friendId) {
        return ResponseEntity.ok(applicationService.findMessagesByFriendIds(id, friendId));
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
    public void deleteSentMessage(String id, String friendId, String messageId) {
        applicationService.deleteSentMessage(id, friendId, messageId);
    }
    @Override
    public void deleteReceivedMessage(String id, String messageId) {
        applicationService.deleteReceivedMessage(id, messageId);
    }

    @Override
    public void deleteChat(String id, String friendId) {
        applicationService.deleteChat(id, friendId);
    }
    @Override
    public void sendMessage(String id, String friendId, String body) {
        applicationService.sendMessage(id, friendId, body);
    }
}
