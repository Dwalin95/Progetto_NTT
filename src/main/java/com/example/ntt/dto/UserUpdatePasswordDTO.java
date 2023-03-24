package com.example.ntt.dto;

import lombok.*;

@Value
public class UserUpdatePasswordDTO {
    String id;
    String oldPassword;
    String newPassword;
    String confirmPassword;
}
