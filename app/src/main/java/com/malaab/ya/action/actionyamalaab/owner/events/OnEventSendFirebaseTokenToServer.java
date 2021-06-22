package com.malaab.ya.action.actionyamalaab.owner.events;

public class OnEventSendFirebaseTokenToServer {

    private String mFirebaseToken;


    public OnEventSendFirebaseTokenToServer(String firebaseToken) {
        mFirebaseToken = firebaseToken;
    }

    public String getFirebaseToken() {
        return mFirebaseToken;
    }
}
