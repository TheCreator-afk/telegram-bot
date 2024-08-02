package com.example.telegrambot;

import java.util.ArrayList;
import java.util.List;

public class UserPhotos {
    private String firstName;
    private String username;
    private List<String> photoFileIds;
    private int photoCount;
    private Integer lastMessageId; // Store the last message ID

    public UserPhotos(String firstName, String username) {
        this.firstName = firstName;
        this.username = username;
        this.photoFileIds = new ArrayList<>();
        this.photoCount = 0;
        this.lastMessageId = null; // Initialize to null
    }

    public void addPhoto(String photoFileId) {
        photoFileIds.add(photoFileId);
        photoCount++;
    }
    public int getPhotoCount() {
        return photoCount;
    }
    public Integer getLastMessageId() {
        return lastMessageId;
    }
    public void setLastMessageId(Integer lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public List<String> getPhotoFileIds() {
        return photoFileIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUsername() {
        return username;
    }
}