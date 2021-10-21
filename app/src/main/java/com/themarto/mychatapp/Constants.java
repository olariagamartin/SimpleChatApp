package com.themarto.mychatapp;

public class Constants {
    public static final int PHONE_NUMBER_LENGTH = 10;

    // Login error codes

    public static final int EMPTY_NUMBER = 3;
    public static final int INVALID_NUMBER = 4;

    public static final int VERIFICATION_FAILED = 6;

    public static final int VERIFICATION_CODE_EMPTY = 11;
    public static final int LOGIN_FAILED = 13;

    public static final int USERNAME_EMPTY = 15;
    public static final int PROFILE_IMAGE_NOT_SET = 17;

    public static final long ONE_MEGABYTE = 1024 * 1024;

    public static class RealtimeDatabasePaths {
        public static String SIMPLE_CHAT_ROOMS = "simpleChatRooms";
        public static String MESSAGES = "messages";
        public static String USERS = "users";
        public static String USERNAME = "name";
        public static String PROFILE_IMAGE_LINK = "profileImageLink";
    }

    public static class StoragePaths {
        public static String IMAGES = "images";
        public static String PROFILE_IMAGE = "profile image";
    }

}
