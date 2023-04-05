package com.example.ntt.dto.post;

import lombok.Getter;

import java.util.Optional;

@Getter
public class PostDTO {

    private String currentUserId;
    private String postId;
    private String title;
    private String body;
    private String imageUrl;

    public Optional<String> getTitle(){
        return Optional.ofNullable(this.title);
    }

    public Optional<String> getBody(){
        return Optional.ofNullable(this.body);
    }

    public Optional<String> getImageUrl(){
        return Optional.ofNullable(this.imageUrl);
    }
}
