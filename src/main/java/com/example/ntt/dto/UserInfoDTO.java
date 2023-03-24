package com.example.ntt.dto;

import lombok.Value;
//import java.util.Optional;
@Value
public class UserInfoDTO {
    String username;
    String firstName;
    String lastName;
    String email;
    String gender;
}
