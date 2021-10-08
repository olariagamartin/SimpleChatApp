package com.themarto.mychatapp.data.network;

public class ContactDTO {
    private String uid;
    private String name;
    private String profileImageLink;
    private boolean online;

    public ContactDTO() { }

    public ContactDTO(String uid, String name, String profileImageLink, boolean online) {
        this.uid = uid;
        this.name = name;
        this.profileImageLink = profileImageLink;
        this.online = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageLink() {
        return profileImageLink;
    }

    public void setProfileImageLink(String profileImageLink) {
        this.profileImageLink = profileImageLink;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
