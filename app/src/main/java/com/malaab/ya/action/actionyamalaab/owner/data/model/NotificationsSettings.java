package com.malaab.ya.action.actionyamalaab.owner.data.model;

public class NotificationsSettings {

    public String firebaseToken;
    public boolean isRegistered;


    public NotificationsSettings(String firebaseToken, boolean isRegistered) {
        this.firebaseToken = firebaseToken;
        this.isRegistered = isRegistered;
    }
}
