package com.example.ntt.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

//@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
//@Builder
public class Post {

    @Id
    private ObjectId _id;
    private String title;
    private String body;
    private Date timestamp;
    private List<Comment> comments;
}
