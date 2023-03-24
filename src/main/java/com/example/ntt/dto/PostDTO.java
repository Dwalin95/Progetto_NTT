package com.example.ntt.dto;

import lombok.Data;

import java.util.Date;
import java.util.Optional;

@Data
public class PostDTO {

    private String currentUserId;
    private String title;
    private String body;
    private Date timestamp;
    private String imageUrl;

    public Optional<String> getImageUrl(){
        return Optional.ofNullable(this.imageUrl);
    }
}
