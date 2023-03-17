package com.example.ntt.projections;

import java.util.Optional;

public interface UserContactInfoProjection {
    Optional<String> getFirstName();
    Optional<String> getLastName();
    Optional<String> getEmail();
    Optional<String> getGender();
}
