package com.example.ntt.dto;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;

@Data
public class PostDTO {
    private String currentUserId;
    private String title;
    private String body;
    private Date timestamp;
    private String imageUrl;

}
