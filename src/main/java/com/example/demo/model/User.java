package com.example.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private String[] friends;
    private Address address;
}




