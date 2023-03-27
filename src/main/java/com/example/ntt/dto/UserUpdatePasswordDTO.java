package com.example.ntt.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdatePasswordDTO {
    String id;
    String oldPassword;
    String newPassword;
    String confirmPassword;
}
