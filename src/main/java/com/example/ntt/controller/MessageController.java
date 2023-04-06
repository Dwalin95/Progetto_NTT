package com.example.ntt.controller;

import com.example.ntt.api.MessageApi;
import com.example.ntt.dto.message.*;
import com.example.ntt.dto.user.CurrentUserFriendIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.Message;
import com.example.ntt.projections.IUsernamePicLastMsg;
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
    public ResponseEntity<Set<IUsernamePicLastMsg>> findAllMessageSenders(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findAllMessageSenders(userId));
    }

    @Override
    public ResponseEntity<List<Message>> userMessagesByFriendId(CurrentUserFriendIdDTO userIds) {
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
    public void deleteSentMessage(MessageSentIdsDTO messageSent) {
        applicationService.deleteSentMessage(messageSent);
    }

    @Override
    public void deleteReceivedMessage(MessageReceivedIdsDTO deleteMessage) { //currentUserId, messageId;
        applicationService.deleteReceivedMessage(deleteMessage);
    }

    @Override
    public void deleteChat(CurrentUserFriendIdDTO userIds) {
        applicationService.deleteChat(userIds);
    }

    @Override
    public void sendMessage(MessageToSendIdsAndBodyDTO messageToSend) {
        applicationService.sendMessage(messageToSend);
    }
}