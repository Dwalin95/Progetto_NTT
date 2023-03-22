package com.example.ntt.api;

import com.example.ntt.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface MessageApi {

    @GetMapping(value = "/{id}/chats")
    ResponseEntity<Set<String>> findAllMessageSenders(@PathVariable String id);

    @GetMapping(value = "/{id}/messages/{friendId}")
    ResponseEntity<List<Message>> findUserMessagesByFriendId(@PathVariable String id, @PathVariable String friendId);

    @PutMapping(value = "/{id}/deleteSentMessage/{friendId}")
    void deleteSentMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String messageId);

    @PutMapping(value = "/{id}/deleteReceivedMessage")
    void deleteReceivedMessage(@PathVariable String id, @RequestParam String messageId);

    @PutMapping(value = "/{id}/deleteChat")
    void deleteChat(@PathVariable String id, @RequestParam String friendId);

    @PostMapping(value = "/{id}/sendMessage/{friendId}")
    void sendMessage(@PathVariable String id, @PathVariable String friendId, @RequestParam String body);
}
