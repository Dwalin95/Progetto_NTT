package com.example.ntt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMsg {

    USER_NOT_FOUND_ERROR_MSG("User: %s not found"),
    NO_FRIENDS_FOUND("No friends found"),
    DELETE_MESSAGE_TIMEOUT("Messages sent more than an hour ago cannot be deleted for both users, it was deleted only for you"),
    NEW_PWS_EQUAL_TO_OLD_PSW("New password can't be equal to the old password"),
    NO_MATCH_OLD_PSW("Entered characters do not match the old password"),
    ACCESS_DENIED("Access denied"),
    URL_IS_NOT_IMG("Entered URL is not an image"),
    USERNAME_ALREADY_IN_USE("The username %s is already in use"),
    EMAIL_ALREADY_IN_USE("The email %s is already in use");

    private final String msg;
}