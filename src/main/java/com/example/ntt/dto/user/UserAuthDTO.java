package com.example.ntt.dto.user;

import lombok.*;

@Getter
public class UserAuthDTO {

    private String email = this.getEmail() == null ? null : this.getEmail().toLowerCase();
    private String password;
}
