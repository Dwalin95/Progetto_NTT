package com.example.ntt.api;

import com.example.ntt.dto.CommentDTO;
import com.example.ntt.dto.PostDTO;
import com.example.ntt.dto.PostIdAndUserIdDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    @PostMapping(value = "/post")
    void createPost(@RequestBody PostDTO post);

    @PostMapping(value = "/comment")
    void createComment(@RequestBody CommentDTO commentDTO);

    @PutMapping(value = "/updatePost")
    void updatePost(@RequestBody PostDTO postDTO);

    @GetMapping(value = "/friendsPosts")
    ResponseEntity<List<Post>> findAllFriendsPosts(@RequestBody UserIdDTO userId);

    @DeleteMapping(value = "/removePost")
    void removePost(@RequestBody PostIdAndUserIdDTO postDTO);
}
