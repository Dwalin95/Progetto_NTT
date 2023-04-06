package com.example.ntt.service;

import com.example.ntt.configuration.UserConfiguration;
import com.example.ntt.dto.comment.CommentDTO;
import com.example.ntt.dto.comment.CommentIdAndPostIdDTO;
import com.example.ntt.dto.post.PostDTO;
import com.example.ntt.dto.post.PostIdAndUserIdDTO;
import com.example.ntt.dto.user.UserIdDTO;
import com.example.ntt.enums.ErrorMsg;
import com.example.ntt.exceptionHandler.ResourceNotFoundException;
import com.example.ntt.model.Comment;
import com.example.ntt.model.Post;
import com.example.ntt.model.User;
import com.example.ntt.projections.post.IPostIdAuthor;
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

    public void createPost(PostDTO postDto){
        mongoService.findUserById(postDto.getCurrentUserId())
                .map(user -> this.addPost(postDto, user))
                .map(mongoService::saveUser)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), postDto.getCurrentUserId())));
    }

    private User addPost(PostDTO postDto, User user) {
        Post post = Post.builder()
                .title(postDto.getTitle().get())
                .body(postDto.getBody().get())
                .timestamp(new Date())
                .imageUrl(postDto.getImageUrl().get())
                .comments(new ArrayList<>())
                .build();

        Optional.of(post)
                .filter(p -> postDto.getImageUrl().isPresent() && !userConfiguration.isImage(postDto.getImageUrl().get()));

        mongoService.savePost(post);
        user.getPostsIds().add(post.get_id());
        return user;
    }

    public void updatePost(PostDTO postDTO){
        mongoService.findPostById(postDTO.getPostId())
                .map(p -> this.handleUpdatePost(postDTO, p))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.POST_NOT_FOUND.getMsg()));
    }

    private Post handleUpdatePost(PostDTO postDto, Post p){
        Optional.of(p)
                .filter(post -> postDto.getImageUrl().isPresent() && !userConfiguration.isImage(postDto.getImageUrl().get()));

        mongoService.savePost(p.withBody(postDto.getBody().orElse(p.getBody()))
                .withTitle(postDto.getTitle().orElse(p.getTitle()))
                .withImageUrl(postDto.getImageUrl().orElse(p.getImageUrl()))
                .withModified(true));
        return p;
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

    //TODO: LDB - non funge
    public List<Post> findAllFriendsPosts(UserIdDTO userId){
        Set<String> friends = mongoService.findUserById(userId.getId())
                        .map(User::getFriends)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMsg.USER_NOT_FOUND_ERROR_MSG.getMsg(), userId.getId())));
        Set<String> postsIds = mongoService.findAllFriendsPostsIdsAggr(friends).stream()
                        .map(IPostIdAuthor::getPostId)
                        .collect(Collectors.toSet());
        return mongoService.findAllPostsByArrAggr(postsIds).stream()
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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMsg.COMMENT_NOT_FOUND.getMsg()));
    }

    private Post updateComments(CommentIdAndPostIdDTO commentIdAndPostId, Post post) {
        List<Comment> comments = mongoService.findCommentListWithoutSpecifiedOneAggr(commentIdAndPostId.getPostId(), commentIdAndPostId.getCommentId());
        post.setComments(comments);
        return post;
    }
}