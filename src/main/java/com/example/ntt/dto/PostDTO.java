package com.example.ntt.dto;

import lombok.Value;
import java.util.Date;

@Value
public class PostDTO {
    String currentUserId;
    String title;
    String body;
    Date timestamp;
    String imageUrl;

}
