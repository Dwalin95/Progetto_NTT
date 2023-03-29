package com.example.ntt.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdatePasswordDTO {
    String id;
    String oldPassword;
    String newPassword;
}
