package com.example.ntt.dto.user;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UserInfoWithIdDTO {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String profilePicUrl;
    private boolean isVisible;

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

    public Optional<Boolean> isVisible() {
        return Optional.of(isVisible);
    }
}