package com.example.ntt.dto;

import lombok.Data;

import java.util.Optional;
@Data
public class UserInfoProjectionDTO {
    Optional<String> Username;
    Optional<String> FirstName;
    Optional<String> LastName;
    Optional<String> Email;
    Optional<String> Gender;
}
