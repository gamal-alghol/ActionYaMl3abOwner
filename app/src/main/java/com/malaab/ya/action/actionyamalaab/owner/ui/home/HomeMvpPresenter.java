package com.malaab.ya.action.actionyamalaab.owner.ui.home;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.Playground;
import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;
import com.malaab.ya.action.actionyamalaab.owner.ui.login.LoginMvpView;


@PerActivity
public interface HomeMvpPresenter<V extends HomeMvpView> extends MvpPresenter<V> {

    void isUserAuthenticated();

    void refreshUserInfo(String userUid);


    void updateCounters();


    void isDeviceRegisteredForNotifications(User user);

    void registerForFirebaseNotifications(User user);


    void isPlaygroundInFavouriteList(Playground playground);

    void addPlaygroundToFavouriteList(Playground playground);

    void removePlaygroundFromFavouriteList(Playground playground);


    void pushFutsalCourts();

    void signOut();
}
