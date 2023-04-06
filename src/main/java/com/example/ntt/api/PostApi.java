package com.example.ntt.api;

import com.example.ntt.dto.comment.CommentDTO;
import com.example.ntt.dto.comment.CommentIdAndPostIdDTO;
import com.example.ntt.dto.post.PostDTO;
import com.example.ntt.dto.post.PostIdAndUserIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    @PostMapping(value = "/post")
    void createPost(@RequestBody PostDTO post);

    @PutMapping(value = "/updatePost")
    void updatePost(@RequestBody PostDTO postDTO);

    @GetMapping(value = "/friendsPosts")
    ResponseEntity<List<Post>> findAllFriendsPosts(@RequestBody UserIdDTO userId);

    @DeleteMapping(value = "/removePost")
    void removePost(@RequestBody PostIdAndUserIdDTO postDTO);

    @PostMapping(value = "/comment")
    void createComment(@RequestBody CommentDTO commentDTO);

    @PutMapping(value = "/deleteComment")
    void deleteComment(@RequestBody CommentIdAndPostIdDTO commentIdAndPostId);
}
