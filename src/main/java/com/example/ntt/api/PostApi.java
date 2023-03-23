package com.example.ntt.api;

import com.example.ntt.dto.PostDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.Post;
import com.example.ntt.model.UpdatedPost;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    //TODO: usare i DTO - FC
    @PostMapping(value = "/post")
    void createPost(@RequestBody PostDTO post);

    @PutMapping(value = "/{id}/removePost")
    void removePost(@PathVariable String id, @RequestParam String postId);

    @PutMapping(value = "/{currentUserId}/updatePost")
    void updatePost(@PathVariable String currentUserId, @RequestParam String postId, @RequestBody UpdatedPost updatedPost);

    /**
     * @param userId {id}
     */
    @GetMapping(value = "/{id}/friendsPosts")
    ResponseEntity<List<Post>> findAllFriendsPosts(@RequestBody UserIdDTO userId);
}
