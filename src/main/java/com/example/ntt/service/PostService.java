package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.comment.CommentDTO;
import com.example.ntt.dto.comment.CommentIdAndPostIdDTO;
import com.example.ntt.dto.post.PostDTO;
import com.example.ntt.dto.post.PostIdAndUserIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.PreconditionFailedException;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.Comment;
import com.example.ntt.model.Post;
import com.example.ntt.model.User;
import com.example.ntt.projections.post.PostIdAndAuthorUsernameProjection;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MongoService mongoService;
    private final UserConfiguration userConfiguration;

    public void createPost(PostDTO postDto){ //TODO: completare l'implementazione - FC [Vedere gli Optional nel return]
        mongoService.findUserById(postDto.getCurrentUserId())
                .map(user -> this.addPost(postDto, user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), postDto.getCurrentUserId())));
    }

    private User addPost(PostDTO postDto, User user) {
        userConfiguration.handleUpdateException(postDto.getImageUrl().isPresent() && !userConfiguration.isImage(postDto.getImageUrl().get()),
                new PreconditionFailedException(ErrorMsg.URL_IS_NOT_IMG.getMsg()));

        Post post = Post.builder()
                .title(postDto.getTitle().get())
                .body(postDto.getBody().get())
                .timestamp(new Date())
                .imageUrl(postDto.getImageUrl().get())
                .comments(new ArrayList<>())
                .build();
        mongoService.savePost(post);
        user.getPostsIds().add(post.get_id());
        return user;
    }

    public void deletePost(PostIdAndUserIdDTO postDto){
        mongoService.findUserById(postDto.getCurrentUserId())
                .map(user -> this.removePost(postDto.getPostId(), user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), postDto.getCurrentUserId())));
    }

    private User removePost(String postId, User user) {
        user.getPostsIds().remove(postId);
        mongoService.deletePost(postId);
        return user;
    }

    public void updatePost(PostDTO postDTO){
        mongoService.findPostById(postDTO.getPostId())
                .map(p -> this.saveUpdatedPost(postDTO, p))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.POST_NOT_FOUND.getMsg()));
    }

    private Post saveUpdatedPost(PostDTO postDto, Post p){
        userConfiguration.handleUpdateException(postDto.getImageUrl().isPresent() && !userConfiguration.isImage(postDto.getImageUrl().get()),
                new PreconditionFailedException(ErrorMsg.URL_IS_NOT_IMG.getMsg()));

        mongoService.savePost(p.withBody(postDto.getBody().orElse(p.getBody()))
                                .withTitle(postDto.getTitle().orElse(p.getTitle()))
                .withImageUrl(postDto.getImageUrl().orElse(p.getImageUrl())));
        return p;
    }

    public List<Post> findAllFriendsPosts(UserIdDTO userId){
        Set<String> friends = mongoService.findUserById(userId.getId())
                        .map(User::getFriends)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())));
        Set<String> postsIds = mongoService.findAllFriendsPostsIdsAggregation(friends).stream().map(PostIdAndAuthorUsernameProjection::getPostId).collect(Collectors.toSet());
        return mongoService.findAllPostsByArrAggregation(postsIds).stream()
                        .sorted(Comparator.comparing(Post::getTimestamp))
                        .collect(Collectors.toList());
    }

    public void createComment(CommentDTO commentDTO){
        mongoService.findPostById(commentDTO.getPostId())
                .map(p -> this.addComment(commentDTO, p))
                .map(mongoService::savePost)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.POST_NOT_FOUND.getMsg()));
    }

    private Post addComment(CommentDTO commentDTO, Post p) {
        Comment comment = Comment.builder()
                ._id(new ObjectId())
                .body(commentDTO.getBody())
                .author(commentDTO.getAuthor())
                .timestamp(new Date())
                .build();

        p.getComments().add(comment);
        return p;
    }

    public void deleteComment(CommentIdAndPostIdDTO commentIdAndPostId) {
        mongoService.findPostById(commentIdAndPostId.getPostId())
                .map(post -> this.updateComments(commentIdAndPostId, post))
                .map(mongoService::savePost)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.POST_NOT_FOUND.getMsg(), commentIdAndPostId.getPostId())));
    }

    private Post updateComments(CommentIdAndPostIdDTO commentIdAndPostId, Post post) {
        List<Comment> comments = mongoService.findCommentListWithoutSpecifiedOne(commentIdAndPostId);
        post.setComments(comments);
        return post;
    }
}