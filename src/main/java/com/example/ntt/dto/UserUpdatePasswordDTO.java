package com.example.ntt.dto;

import lombok.*;

@Data
public class UserUpdatePasswordDTO {
    private String id;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
