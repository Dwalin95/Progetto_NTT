package com.example.ntt.service;

import com.example.ntt.dto.PostDTO;
import com.example.ntt.dto.UserIdDTO;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.Post;
import com.example.ntt.model.UpdatedPost;
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

    public void createPost(PostDTO post){
//        mongoService.findUserById(post.getCurrentUserId())
//                .map(user -> addPost(post, user))
//                .map(mongoService::saveUser)
//                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, id)));
    }

    private User addPost(Post post, User user) {
        post.with_id(new ObjectId())
            .withTimestamp(new Date());
        user.getPosts().add(post);
        return user;
    }
//    private User addPost(Post post, User user) {
//        post.with_id(new ObjectId())
//            .withTimestamp(new Date());
//        user.getPosts().add(post);
//        return user;
//    }

    public void deletePost(String currentUserId, String postId){
        mongoService.findUserById(currentUserId)
                .map(user -> removePost(postId, user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }

    private User removePost(String postId, User user) {
        List<Post> posts = mongoService.getPostListWithoutSpecifiedPost(user.get_id(), postId);
        user.setPosts(posts);
        return user;
    }

    public void updatePost(String currentUserId, String postId, UpdatedPost updatedPost){
        mongoService.findUserById(currentUserId)
                .map(u -> handleUpdatePost(postId, updatedPost, u))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, currentUserId)));
    }
    //TODO: DTO - FC
    private User handleUpdatePost(String postId, UpdatedPost updatedPost, User u) {
        List<Post> posts = mongoService.getPostListWithoutSpecifiedPost(u.get_id(), postId);
        posts.add(mongoService.updatedPost(u.get_id(), postId, updatedPost.getTitle(), updatedPost.getBody()));
        u.setPosts(posts);
        return u;
    }

    //TODO: da testare
    public List<Post> findAllFriendsPosts(UserIdDTO userId){
        Set<User> friends = mongoService.findUserById(userId.getId())
                        .map(u -> mongoService.findUserFriendsById(u.getFriends()))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_ERROR_MSG, userId.getId())))
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No friends found", userId.getId())));
        return mongoService.findAllPostsByArrayAggregation(friends).stream()
                        .sorted(Comparator.comparing(Post::getTimestamp))
                        .collect(Collectors.toList());
    }
}
