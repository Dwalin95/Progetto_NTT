package com.example.ntt.dto;

import lombok.Value;
import java.util.Date;
import java.util.Optional;

@Value
public class PostDTO {

    String currentUserId;
    String title;
    String body;
    Date timestamp;
    String imageUrl;

    public Optional<String> getImageUrl(){
        return Optional.ofNullable(this.imageUrl);
    }
}
