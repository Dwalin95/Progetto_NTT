package com.example.ntt.projections;

import java.util.Set;

public interface UserFriendsAndRequestReceivedListProjection {
    Set<String> getFriends();
    Set<String> getReceivedFriendRequests();

}
