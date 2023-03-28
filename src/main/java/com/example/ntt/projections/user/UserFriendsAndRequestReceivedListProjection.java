package com.example.ntt.projections.user;

import java.util.Set;

public interface UserFriendsAndRequestReceivedListProjection {
    Set<String> getFriends();
    Set<String> getReceivedFriendRequests();

}
