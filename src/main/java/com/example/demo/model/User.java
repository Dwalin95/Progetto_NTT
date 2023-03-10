package com.example.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
@Document (collection = "users" )
public class User{

    @Id
    private String _id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String pwz;
    private String gender;
    private List<String> friends = new ArrayList<>();
    private List<String> receivedFriendRequests = new ArrayList<>();
    private List<String> sentFriendRequests = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private Address address;
}
