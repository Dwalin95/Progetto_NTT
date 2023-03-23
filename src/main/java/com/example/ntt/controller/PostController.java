package com.example.ntt.controller;

import com.example.ntt.api.PostApi;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.Post;
import com.example.ntt.model.UpdatedPost;
import com.example.ntt.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class PostController implements PostApi {

    private final ApplicationService applicationService;

    @Override
    public void createPost(String currentUserId, Post post) {
        applicationService.createPost(currentUserId, post);
    }

    @Override
    public void removePost(String currentUserId, String postId) {
        applicationService.deletePost(currentUserId, postId);
    }

    //TODO: DTO - FC
    @Override
    public void updatePost(String currentUserId, String postId, UpdatedPost updatedPost) {
        applicationService.updatePost(currentUserId, postId, updatedPost);
    }

    @Override
    public ResponseEntity<List<Post>> findAllFriendsPosts(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findAllFriendsPosts(userId));
    }

}
