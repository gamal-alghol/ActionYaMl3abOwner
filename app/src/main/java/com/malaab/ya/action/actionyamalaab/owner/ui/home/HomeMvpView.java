package com.malaab.ya.action.actionyamalaab.owner.ui.home;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;


public interface HomeMvpView extends MvpView {

    void onUserAuthenticationSuccess(String userUid);
    void onRefreshUserInfo(User user);

    void onUpdateNotificationsCounter(int count);
    void onUpdateMessagesCounter(int count);

    void onRegisterDeviceForNotification(User user);

    void onPlaygroundInFavouriteList(boolean isFavourite);
    void onAddPlaygroundToFavouriteList(boolean isSuccess);
    void onRemovePlaygroundFromFavouriteList(boolean isSuccess);
}
