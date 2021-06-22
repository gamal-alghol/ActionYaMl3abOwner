package com.malaab.ya.action.actionyamalaab.owner.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.malaab.ya.action.actionyamalaab.owner.data.model.NotificationsSettings;
import com.malaab.ya.action.actionyamalaab.owner.events.OnEventSendFirebaseTokenToServer;
import com.malaab.ya.action.actionyamalaab.owner.utils.Constants;
import com.malaab.ya.action.actionyamalaab.owner.utils.JsonConverter;

import org.greenrobot.eventbus.EventBus;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        EventBus.getDefault().post(new OnEventSendFirebaseTokenToServer(token));
    }


    private void storeRegIdInPref(String token) {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        String json = JsonConverter.objectToJson(new NotificationsSettings(token, false));
        if (json != null) {
            mPrefs.edit().putString(Constants.KEY_NOTIFICATIONS_SETTINGS, json).apply();
        }
    }
}