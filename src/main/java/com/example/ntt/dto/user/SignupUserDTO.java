package com.example.ntt.dto.user;

import lombok.Getter;

@Getter
public class SignupUserDTO { //TODO: FC - Da implementare

    private String username;
    private String email;
    private String password;
    private String profilePicUrl;
    private String firstName;
    private String lastName;
    private String address; //TODO: FC - verificare se usarlo come Address o in un altro modo
}