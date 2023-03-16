package com.example.ntt.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

//@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
public class Comment {

    @Id
    private ObjectId _id;
    private String body;
    private String author;
    private Date timestamp;
}
