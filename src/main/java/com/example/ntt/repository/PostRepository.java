package com.example.ntt.repository;

import com.example.ntt.model.Post;
import com.example.ntt.model.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends MongoRepository<User, String> {

    //TODO: LDB - da vedere perché $in non funziona (solo qua)
    @Aggregation(
            pipeline = {"{$match: {'_id': {$in: ?0}}}", "{$unwind: {path: \"$posts\"}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\", imageUrl: \"$posts.imageUrl\"}}"}
    )
    List<Post> findAllPostsByFriendIdsArr(Set<String> friendsId);

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}", "{$match: {_id: {$ne: ObjectId(?1)}}}"}
    )
    List<Post> getPostListWithoutSpecifiedPost(String currentUserId, String postId);

    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$unwind: {path: \"$posts\"}}", "{$project: {_id: \"$posts._id\", title: \"$posts.title\", body: \"$posts.body\", timestamp: \"$posts.timestamp\", comments: \"$posts.comments\"}}", "{$match: {_id: ObjectId(?1)}}", "{$set: {title: ?2, body: ?3, modified: true}}"}
    )
    Post updatedPost(String currentUserId, String postId, String title, String body);
}
