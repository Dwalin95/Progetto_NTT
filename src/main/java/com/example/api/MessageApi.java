package com.example.api;

import com.example.ntt.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface MessageApi {

    @GetMapping(value = "/{currentUserId}/messages")
    ResponseEntity<Set<String>> findAllMessageSenders(@PathVariable String currentUserId);

    @GetMapping(value = "/{currentUserId}/messages/{friendId}")
    ResponseEntity<List<Message>> findUserMessagesByFriendId(@PathVariable String currentUserId, @PathVariable String friendId);

    @GetMapping(value = "/{currentUserId}/messages")
    ResponseEntity<List<Message>> findMessageByTextGlobal(@PathVariable String currentUserId, @RequestParam String text);

    @GetMapping(value = "/{currentUserId}/messages/{friendId}")
    ResponseEntity<List<Message>> findMessageByTextPerFriend(@PathVariable String currentUserId, @PathVariable String friendId, @RequestParam String text);

    @PutMapping(value = "/{currentUserId}/deleteSentMessage/{friendId}")
    void deleteSentMessage(@PathVariable String currentUserId, @PathVariable String friendId, @RequestParam String messageId);

    @PutMapping(value = "/{currentUserId}/deleteReceivedMessage")
    void deleteReceivedMessage(@PathVariable String currentUserId, @RequestParam String messageId);

    @PutMapping(value = "/{currentUserId}/deleteChat")
    void deleteChat(@PathVariable String currentUserId, @RequestParam String friendId);

    @PostMapping(value = "/{currentUserId}/sendMessage/{friendId}")
    void sendMessage(@PathVariable String currentUserId, @PathVariable String friendId, @RequestParam String body);
}
