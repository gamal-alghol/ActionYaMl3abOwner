package com.malaab.ya.action.actionyamalaab.owner.ui.login;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;


public interface LoginMvpView extends MvpView {

    void onServerLoginSuccess(String userUId);

    void onUserExistInDB(User user);
    void onUserNotExistInDB();

    void onResetPassword(String message);
}
