package com.example.ntt.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {

    private String postId;
    private String body;
    private String author;
}
