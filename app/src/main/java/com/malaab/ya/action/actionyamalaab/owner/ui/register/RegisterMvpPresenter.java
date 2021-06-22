package com.malaab.ya.action.actionyamalaab.owner.ui.register;

import com.malaab.ya.action.actionyamalaab.owner.data.network.model.User;
import com.malaab.ya.action.actionyamalaab.owner.di.PerActivity;
import com.malaab.ya.action.actionyamalaab.owner.ui.base.MvpPresenter;


@PerActivity
public interface RegisterMvpPresenter<V extends RegisterMvpView> extends MvpPresenter<V> {

    void getPasswordStrength(String password);


    void doServerRegister(String fName, String lName, String email, String phone, String password, String referredBy);

    void generateUserUniqueId(User user);

    void addUserToFirebaseDatabase(User user);

}
