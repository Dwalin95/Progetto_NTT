package com.example.api;

import com.example.ntt.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    @PostMapping(value = "/{id}/createPost")
    void createPost(@PathVariable String id, @RequestBody Post post);

    /** in sviluppo
    @PutMapping(value = "{id}/removePost")
    void removePost(@PathVariable String id, String postId);
*/
    @GetMapping(value = "/{id}/friendsPost")
    ResponseEntity<List<Post>> findAllFriendsPost(@PathVariable String id);

}
