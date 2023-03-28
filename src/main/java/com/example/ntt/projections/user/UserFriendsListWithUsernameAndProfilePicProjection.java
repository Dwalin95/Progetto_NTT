package com.example.ntt.projections.user;

import java.util.Optional;

public interface UserFriendsListWithUsernameAndProfilePicProjection {
    Optional<String> username();
    Optional<String> profilePicUrl();
}
