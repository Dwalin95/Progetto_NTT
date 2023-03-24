package com.example.ntt.dto;

import lombok.Value;

import java.util.Optional;

@Value
public class UserInfoWithIdDTO {
    String id;
    String username;
    String firstName;
    String lastName;
    String email;
    String gender;
    String profilePicUrl;

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getGender() {
        return Optional.ofNullable(gender);
    }

    public Optional<String> getProfilePicUrl() {
        return Optional.ofNullable(profilePicUrl);
    }
}