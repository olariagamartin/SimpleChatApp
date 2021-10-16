package com.themarto.mychatapp.data.domain;

import android.graphics.Bitmap;

import java.util.Objects;

public class ContactModel {
    private String id;
    private String name;
    private Bitmap profileImage;
    private boolean online;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactModel that = (ContactModel) o;
        return online == that.online && id.equals(that.id) && name.equals(that.name) && profileImage.equals(that.profileImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, profileImage, online);
    }
}
