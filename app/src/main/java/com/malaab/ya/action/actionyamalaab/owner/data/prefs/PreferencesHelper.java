package com.malaab.ya.action.actionyamalaab.owner.data.prefs;

import com.malaab.ya.action.actionyamalaab.owner.data.model.AppSettings;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Counter;
import com.malaab.ya.action.actionyamalaab.owner.data.model.FirebaseSettings;
import com.malaab.ya.action.actionyamalaab.owner.data.model.UserFavouritePlaygrounds;
import com.malaab.ya.action.actionyamalaab.owner.data.model.Notifications;
import com.malaab.ya.action.actionyamalaab.owner.data.model.NotificationsSettings;
import com.malaab.ya.action.actionyamalaab.owner.data.model.UsersFavouritePlaygrounds;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Notification;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;

import java.util.List;


public interface PreferencesHelper {

    void setAppSettings(AppSettings appSettings);

    AppSettings getAppSettings();


    void setFirebaseSettings(FirebaseSettings firebaseSettings);

    FirebaseSettings getFirebaseSettings();


    //region COUNTERS
    void setCounters(Counter counters);

    void resetNotificationsCounters();

    void resetMessagesCounters();

    Counter getCounters();
    //endregion


    //region NOTIFICATIONS
    void setNotificationsSettings(String firebaseToken, boolean isRegistered);

    void updateNotificationsSettings(String firebaseToken, boolean isRegistered);

    NotificationsSettings getNotificationsSettings();


    void addNotificationContent(Notification notification);

    void removeNotificationContent(Notification notification);

    Notifications getNotifications();
    //endregion


    //region USER
    void setCurrentUser(User user);

    User getCurrentUser();
    //endregion


    //region FAVOURITE_PLAYGROUND
    boolean isPlaygroundInFavouriteList(String userUid, Playground playground);

    void addPlaygroundToFavouriteList(String userUid, Playground playground);

    void removePlaygroundTFromFavouriteList(String userUid, Playground playground);

    UserFavouritePlaygrounds getUserFavouritePlaygrounds(String userUid);

    UsersFavouritePlaygrounds getUsersFavouritePlaygrounds();

    //endregion

}
