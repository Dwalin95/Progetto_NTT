package com.example.api;

import com.example.ntt.model.Post;
import com.example.ntt.model.UpdatedPost;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    @PostMapping(value = "/{id}/createPost")
    void createPost(@PathVariable String id, @RequestBody Post post);

    @PutMapping(value = "/{id}/removePost")
    void removePost(@PathVariable String id, @RequestParam String postId);

    @PutMapping(value = "/{currentUserId}/updatePost")
    void updatePost(@PathVariable String currentUserId, @RequestParam String postId, @RequestBody UpdatedPost updatedPost);

    @GetMapping(value = "/{id}/friendsPost")
    ResponseEntity<List<Post>> findAllFriendsPost(@PathVariable String id);
}
