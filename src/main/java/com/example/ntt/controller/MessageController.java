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
    public ResponseEntity<Set<String>> findAllMessageSenders(@PathVariable String id) {
        return ResponseEntity.ok(applicationService.findAllMessageSenders(id));
    }

    @Override
    public ResponseEntity<List<Message>> findUserMessagesByFriendId(@PathVariable String id, @PathVariable String friendId) {
        return ResponseEntity.ok(applicationService.findMessagesByFriendIds(id, friendId));
    }

    @Override
    public void deleteSentMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String messageId) {
        applicationService.deleteSentMessage(id, friendId, messageId);
    }
    @Override
    public void deleteReceivedMessage(@PathVariable String id, @RequestParam String messageId) {
        applicationService.deleteReceivedMessage(id, messageId);
    }

    @Override
    public void deleteChat(@PathVariable String id, @RequestParam String friendId) {
        applicationService.deleteChat(id, friendId);
    }
    @Override
    public void sendMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String body) {
        applicationService.sendMessage(id, friendId, body);
    }
}
