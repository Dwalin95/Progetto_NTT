package com.example.ntt.repository;

import com.example.ntt.dto.comment.CommentIdAndPostIdDTO;
import com.example.ntt.model.Comment;
import com.example.ntt.model.Post;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    @Query("{'_id': {$in: ?0}}")
    List<Post> findAllPostsByIdsArr(Set<String> postsIds);

    //TODO: cancellare il refuso
    @Aggregation(
            pipeline = {"{$match: {_id: ObjectId(?0)}}", "{$set: {title: ?1, body: ?2, imageUrl: ?3, modified: true}}"}
    )
    Post updatedPost(String postId, String title, String body, String imageUrl);

    //questa ti serve per fare la delete del messaggio,
    //restituisce una lista di messaggi SENZA quello di cui gli dai l'id.
    //I commenti farei che si possono solo cancellare e non modificare quindi non c'è bisogno dell'update
    //Restituisce la lista di commenti senza il commento da eliminare.
    @Aggregation(
            pipeline = {
                    "{$match: " +
                    "{_id: ObjectId(?0)}}",
                    "{$unwind: " +
                            "{path: \"$comments\"}}",
                    "{$project: " +
                            "{_id: \"$comments._id\", " +
                            "body: \"$comments.body\", " +
                            "author: \"$comments.author\", " +
                            "timestamp: \"$comments.timestamp\"}}",
                    "{$match: " +
                            "{_id: " +
                            "{$ne: ObjectId(?1)}}}"
            }
    )
    List<Comment> findCommentListWithoutSpecifiedOne(CommentIdAndPostIdDTO commentIdAndPostId);
}
