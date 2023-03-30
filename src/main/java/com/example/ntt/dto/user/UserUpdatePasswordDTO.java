package com.example.ntt.dto.user;

import lombok.*;

@Getter
public class UserUpdatePasswordDTO {

    private String id;
    private String oldPassword;
    private String newPassword;
}