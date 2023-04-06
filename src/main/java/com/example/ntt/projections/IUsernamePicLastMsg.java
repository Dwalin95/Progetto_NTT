package com.example.ntt.projections;

import com.example.ntt.model.Message;

public interface IUsernamePicLastMsg {

    String getUsername();
    String getProfilePicUrl();
    Message getMessages();
}