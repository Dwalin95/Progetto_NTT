package com.example.ntt.controller;

import com.example.ntt.api.PostApi;
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
    public void createPost(@PathVariable String id, @RequestBody Post post) {
        applicationService.createPost(id, post);
    }

    /**  in sviluppo
    @Override
    public void removePost(String id, String postId) {
        applicationService.removePost(id, postId);
    }
*/
    @Override
    public ResponseEntity<List<Post>> findAllFriendsPost(@RequestBody UserIdDTO userId) {
        return ResponseEntity.ok(applicationService.findAllFriendsPosts(userId));
    }

}
