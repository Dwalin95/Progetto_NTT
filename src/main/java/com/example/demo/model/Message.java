package com.example.demo.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@With
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@Builder
public class Message {

    private String body;
    private Date timestamp;
    private String senderUsername;
    private String receiverUsername;
}