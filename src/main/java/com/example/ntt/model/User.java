package com.example.ntt.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String password;
    private String gender;
    private String profilePicUrl;
    private Set<String> friends = new HashSet<>();
    private Set<String> receivedFriendRequests = new HashSet<>();
    private Set<String> sentFriendRequests = new HashSet<>();
    private List<Message> messages = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();
    private Address address;
}
