package com.example.ntt.projections.user;

import java.util.Optional;

public interface UserContactInfoProjection {
    Optional<String> getFirstName();
    Optional<String> getLastName();
    Optional<String> getEmail();
    Optional<String> getGender();
}
