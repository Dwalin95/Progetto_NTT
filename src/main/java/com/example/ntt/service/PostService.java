package com.example.ntt.service;

import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.Post;
import com.example.ntt.model.User;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MongoService mongoService;
    private static final String USER_NOT_FOUND_ERROR_MSG = "User: %s not found";

    public void createPost(String id, Post post){
        mongoService.findUserById(id)
                .map(user -> addPost(post, user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User addPost(Post post, User user) {
        post.set_id(new ObjectId());
        post.setTimestamp(new Date());
        user.getPosts().add(post);
        return user;
    }

    //TODO: da testare
    public void removePost(String id, String postId){
        mongoService.findUserById(id)
                .map(user -> removePost(id, postId, user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User removePost(String id, String postId, User user) {
        Post postToRemove = mongoService.findSinglePostAggregation(id, postId);
        user.getPosts().remove(postToRemove);
        return user;
    }

    public void updatePost(String id, String postId, Optional<String> title, Optional<String> body){
        //TODO: in progress
    }

    //TODO: da testare
    public List<Post> findAllFriendsPosts(String id){
        Set<User> friends = mongoService.findUserById(id)
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No friends found", id)));
        return mongoService.findAllPostsByArrayPostsAggregation(friends).stream()
                        .sorted(Comparator.comparing(Post::getTimestamp))
                        .collect(Collectors.toList());
    }
}
