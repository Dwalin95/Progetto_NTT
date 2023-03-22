package com.example.ntt.controller;

import com.example.ntt.api.PostApi;
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
    public void createPost(String id, Post post) {
        applicationService.createPost(id, post);
    }

    @Override
    public void removePost(String id, String postId) {
        applicationService.deletePost(id, postId);
    }

    @Override
    public void updatePost(String currentUserId, String postId, UpdatedPost updatedPost){
        applicationService.updatePost(currentUserId, postId, updatedPost);
    }

    @Override
    public ResponseEntity<List<Post>> findAllFriendsPost(String id) {
        return ResponseEntity.ok(applicationService.findAllFriendsPosts(id));
    }

}
