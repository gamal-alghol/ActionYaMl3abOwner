package com.malaab.ya.action.actionyamalaab.owner.ui.user;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpView;


public interface UsersDetailsMvpView extends MvpView {

    void onGetUserDetails(User user);
}
