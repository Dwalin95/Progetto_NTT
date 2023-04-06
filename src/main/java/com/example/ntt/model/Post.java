package com.example.ntt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {

    @Id
    private String _id;
    private String title;
    private String body;
    private Date timestamp;
    private String imageUrl;
    private boolean modified = false;
    private List<Comment> comments = new ArrayList<>();
}
