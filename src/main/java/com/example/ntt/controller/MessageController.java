package com.example.ntt.controller;

import com.example.ntt.api.MessageApi;
import com.example.ntt.dto.message.*;
import com.example.ntt.dto.user.CurrentUserIdAndFriendIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
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

    @Override
    public ResponseEntity<List<Message>> findMessageByTextGlobal(MessageTextAndCurrentUserIdDTO messageByText) {
        return ResponseEntity.ok(applicationService.findMessageByTextGlobal(messageByText));
    }

    @Override
    public ResponseEntity<List<Message>> findMessageByTextPerFriend(MessageTextAndCurrentUserAndFriendIdDTO messageByText) {
        return ResponseEntity.ok(applicationService.findMessageByTextPerFriend(messageByText));
    }

    @Override
    public void deleteSentMessage(@RequestBody MessageSentIdsDTO messageSent) {
        applicationService.deleteSentMessage(messageSent);
    }

    @Override
    public void deleteReceivedMessage(@RequestBody MessageReceivedIdsDTO deleteMessage) { //currentUserId, messageId;
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
