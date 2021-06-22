package com.malaab.ya.action.actionyamalaab.owner.ui.user;

import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface UsersDetailsMvpPresenter<V extends UsersDetailsMvpView> extends MvpPresenter<V> {

    void getUserDetails(String userUid);
}
