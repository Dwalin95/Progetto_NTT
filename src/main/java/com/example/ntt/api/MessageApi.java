package com.example.ntt.api;

import com.example.ntt.dto.message.*;
import com.example.ntt.dto.user.CurrentUserFriendIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.Message;
import com.example.ntt.projections.IUsernamePicLastMsg;
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

    @GetMapping(value = "/chats")
    ResponseEntity<Set<IUsernamePicLastMsg>> findAllMessageSenders(@RequestBody UserIdDTO userId);

    @GetMapping(value = "/friends/messages")
    ResponseEntity<List<Message>> userMessagesByFriendId(@RequestBody CurrentUserFriendIdDTO userIds);

    @PutMapping(value = "/deleteSentMessage")
    void deleteSentMessage(@RequestBody MessageSentIdsDTO messageSent);

    @PutMapping(value = "/deleteReceivedMessage")
    void deleteReceivedMessage(@RequestBody MessageReceivedIdsDTO messageReceived); //ex @PathVariable String id, @RequestParam String messageId

    @PutMapping(value = "/deleteChat")
    void deleteChat(@RequestBody CurrentUserFriendIdDTO userIds);

    @PostMapping(value = "/sendMessage")
    void sendMessage(@RequestBody MessageToSendIdsAndBodyDTO messageToSend);
}
