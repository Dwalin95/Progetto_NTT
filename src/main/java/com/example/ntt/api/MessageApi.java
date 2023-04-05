package com.example.ntt.api;

import com.example.ntt.dto.message.*;
import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface MessageApi {

    @GetMapping(value = "/user/textGlobal")
    ResponseEntity<List<Message>> findMessageByTextGlobal(@RequestBody MessageTextAndCurrentUserIdDTO messageByText);

    @GetMapping(value = "/user/textPerChat")
    ResponseEntity<List<Message>> findMessageByTextPerFriend(@RequestBody MessageTextAndCurrentUserAndFriendIdDTO messageByText);

    /**
     * @param userId {id}
     * @return
     */
    //TODO: FC - deve ritornare immagine profilo e lo username (possibilmente anche l'ultimo messaggio inviato nella chat)
    @GetMapping(value = "/chats")
    ResponseEntity<Set<String>> findAllMessageSenders(@RequestBody UserIdDTO userId);

    /**
     * @param userIds {currentUserId, friendId}
     * @return //il ritorno va bene cosi
     */
    @GetMapping(value = "/friends/messages")
    ResponseEntity<List<Message>> userMessagesByFriendId(CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param messageSent {id, friendId, messageId}
     */
    @PutMapping(value = "/deleteSentMessage")
    void deleteSentMessage(@RequestBody MessageSentIdsDTO messageSent);

    /**
     * @param messageReceived {currentUserId, messageId}
     */
    @PutMapping(value = "/deleteReceivedMessage")
    void deleteReceivedMessage(@RequestBody MessageReceivedIdsDTO messageReceived); //ex @PathVariable String id, @RequestParam String messageId

    /**
     * @param userIds {id, friendId}
     */
    @PutMapping(value = "/deleteChat")
    void deleteChat(@RequestBody CurrentUserIdAndFriendIdDTO userIds);

    /**
     * @param messageToSend {currentUserId, friendId, body}
     */
    @PostMapping(value = "/sendMessage")
    void sendMessage(@RequestBody MessageToSendIdsAndBodyDTO messageToSend);
}
