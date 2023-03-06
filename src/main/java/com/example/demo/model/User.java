package com.example.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "users" )
public class User{

    @Id
    private String _id;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private String pwz;
    private String gender;
    private Address address;
}




