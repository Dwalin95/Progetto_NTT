package com.example.ntt.api;

import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.model.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public interface PostApi {

    @PostMapping(value = "/post")
    void createPost(@PathVariable String id, @RequestBody Post post);

    /** in sviluppo
    @PutMapping(value = "{id}/removePost")
    void removePost(@PathVariable String id, String postId);
*/
    /**
     * @param userId {id}
     */
    @GetMapping(value = "/{id}/friendsPost")
    ResponseEntity<List<Post>> findAllFriendsPost(@RequestBody UserIdDTO userId);
}
