package com.example.ntt.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class UserInfoWithIdDTO {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String ProfilePicUrl;
}
