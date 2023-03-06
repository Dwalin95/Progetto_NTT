package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "users" )
public class User {

    @Id
    private String _id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private Address address;

}




