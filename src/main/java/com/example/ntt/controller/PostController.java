package com.example.ntt.controller;

import com.example.ntt.api.PostApi;
import com.example.ntt.dto.CommentDTO;
import com.example.ntt.dto.PostDTO;
import com.example.ntt.dto.PostIdAndUserIdDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.Post;
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
    public void createPost(PostDTO post) {
        applicationService.createPost(post);
    }

    @Override
    public void removePost(PostIdAndUserIdDTO postDTO) {
        applicationService.deletePost(postDTO);
    }

    @Override
    public void updatePost(PostDTO postDTO) {
        applicationService.updatePost(postDTO);
    }

    @Override
    public ResponseEntity<List<Post>> findAllFriendsPosts(UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findAllFriendsPosts(userId));
    }

    @Override
    public void createComment(CommentDTO commentDTO){
        applicationService.createComment(commentDTO);
    }
}
