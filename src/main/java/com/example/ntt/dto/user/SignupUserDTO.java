package com.example.ntt.dto.user;

import lombok.Value;

@Value
public class SignupUserDTO { //TODO: Da implementare - FC
    String username;
    String email;
    String password;
    String profilePicUrl;
    String firstName;
    String lastName;
    String address; //TODO: verificare se usarlo come Address o in un altro modo

}