package com.example.ntt.api;

import com.example.ntt.dto.*;
import com.example.ntt.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface MessageApi {

    /**
     * @param userId {id}
     * @return
     */
    @GetMapping(value = "/chats")
    ResponseEntity<Set<String>> findAllMessageSenders(@RequestBody UserIdDTO userId);

    /**
     * @param userIds {currentUserId, friendId}
     * @return
     */
    @GetMapping(value = "/friends/messages")
    ResponseEntity<List<Message>> userMessagesByFriendId(CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param messageSent {id, friendId, messageId}
     * String id,
     * String friendId,
     * String messageId
     */
    @PutMapping(value = "/deleteSentMessage")
    void deleteSentMessage(@RequestBody MessageSentIdsDTO messageSent);

    /**
     * @param messageReceived {currentUserId, messageId}
     */
    @PutMapping(value = "/deleteReceivedMessage")
    void deleteReceivedMessage(@RequestBody MessageIdsDTO messageReceived); //ex @PathVariable String id, @RequestParam String messageId

    /**
     * @param userIds {id, friendId}
     * @param id
     * @param friendId
     */
    @PutMapping(value = "/{id}/deleteChat")
    void deleteChat(@RequestBody CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param messageToSend {currentUserId, friendId, body}
     */
    @PostMapping(value = "/{id}/sendMessage/{friendId}")
    void sendMessage(@RequestBody MessageToSendIdsAndBodyDTO messageToSend);
}
